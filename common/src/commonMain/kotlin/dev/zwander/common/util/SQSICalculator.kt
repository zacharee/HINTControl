package dev.zwander.common.util

import kotlin.math.exp
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

/**
 * Signal Quality/Speed Index (SQSI) Calculator
 * 
 * Provides a 1-10 scale metric that combines:
 * - RSRP/SS-RSRP (coverage)
 * - RSRQ/SS-RSRQ (interference/cell loading)
 * - SINR/SS-SINR (link quality)
 * - CQI (MCS the network thinks will work)
 * - Channel bandwidth
 */
object SQSICalculator {
    
    // CQI to spectral efficiency tables (bits/Hz)
    private val lteCqiTable = mapOf(
        0 to 0.0,
        1 to 0.1523,
        2 to 0.2344,
        3 to 0.3770,
        4 to 0.6016,
        5 to 0.8770,
        6 to 1.1758,
        7 to 1.4766,
        8 to 1.9141,
        9 to 2.4063,
        10 to 2.7305,
        11 to 3.3223,
        12 to 3.9023,
        13 to 4.5234,
        14 to 5.1152,
        15 to 5.5547
    )
    
    private val nrCqiTable = mapOf(
        0 to 0.0,
        1 to 0.1523,
        2 to 0.2344,
        3 to 0.3770,
        4 to 0.6016,
        5 to 0.8770,
        6 to 1.1758,
        7 to 1.4766,
        8 to 1.9141,
        9 to 2.4063,
        10 to 2.7305,
        11 to 3.3223,
        12 to 3.9023,
        13 to 4.5234,
        14 to 5.1152,
        15 to 7.4063  // With 256-QAM enabled
    )
    
    private const val LTE_MAX_SE = 5.5547
    private const val NR_MAX_SE = 7.4063
    
    data class RatData(
        val rsrp: Int?,      // dBm
        val rsrq: Int?,      // dB
        val sinr: Int?,      // dB
        val cqi: Int?,       // 0-15
        val bandwidth: Int?  // MHz
    )
    
    /**
     * Calculate SQSI for a single RAT or combined LTE+5G
     * 
     * @param lteData LTE measurements (null if not connected)
     * @param nrData 5G NR measurements (null if not connected)
     * @return SQSI value from 1.0 to 10.0, or null if no data available
     */
    fun calculateSQSI(lteData: RatData?, nrData: RatData?): Float? {
        if (lteData == null && nrData == null) return null
        
        val activeRats = mutableListOf<Pair<RatData, Boolean>>() // data, isNR
        lteData?.let { activeRats.add(it to false) }
        nrData?.let { activeRats.add(it to true) }
        
        // Calculate total bandwidth for weighting
        val totalBandwidth = activeRats.sumOf { it.first.bandwidth ?: 20 }.toFloat()
        if (totalBandwidth <= 0) return null
        
        var weightedL = 0.0f
        var weightedC = 0.0f
        var weightedI = 0.0f
        
        for ((data, isNR) in activeRats) {
            val bw = (data.bandwidth ?: 20).toFloat()
            val weight = bw / totalBandwidth
            
            // Normalize metrics
            val rsrpNorm = data.rsrp?.let { normalizeRsrp(it) } ?: 0.5f
            val rsrqNorm = data.rsrq?.let { normalizeRsrq(it) } ?: 0.5f
            val sinrNorm = data.sinr?.let { normalizeSinr(it) } ?: 0.5f
            
            // Calculate link subscore (L)
            val linkScore = if (data.cqi != null) {
                val seNorm = normalizeSpectralEfficiency(data.cqi, isNR)
                0.7f * seNorm + 0.3f * sinrNorm
            } else {
                sinrNorm
            }
            
            // Weight by bandwidth share
            weightedL += weight * linkScore
            weightedC += weight * rsrpNorm  // Coverage
            weightedI += weight * rsrqNorm  // Interference
        }
        
        // Capacity tilt for total bandwidth
        val B = 1.0f - exp(-totalBandwidth / 40.0f).toFloat()
        
        // Composite index (0-1)
        val S = 0.50f * weightedL + 0.25f * weightedC + 0.15f * weightedI + 0.10f * B
        
        // Convert to 1-10 scale
        val sqsi = 1.0f + 9.0f * clip(S, 0.0f, 1.0f)
        
        return sqsi
    }
    
    /**
     * Calculate SQSI and return as integer (1-10)
     */
    fun calculateSQSIInt(lteData: RatData?, nrData: RatData?): Int? {
        return calculateSQSI(lteData, nrData)?.roundToInt()
    }
    
    /**
     * Get color for SQSI value (1-10 scale)
     * Returns a value between 0 (red) and 1 (green) for color interpolation
     */
    fun getSQSIColorFraction(sqsi: Float): Float {
        // Map 1-10 to 0-1 for color gradient
        return clip((sqsi - 1.0f) / 9.0f, 0.0f, 1.0f)
    }
    
    // Normalization functions
    
    private fun normalizeRsrp(rsrp: Int): Float {
        // Maps -120 to -80 dBm to 0 to 1
        return clip((rsrp + 120) / 40.0f, 0.0f, 1.0f)
    }
    
    private fun normalizeRsrq(rsrq: Int): Float {
        // Maps -20 to -5 dB to 0 to 1
        return clip((rsrq + 20) / 15.0f, 0.0f, 1.0f)
    }
    
    private fun normalizeSinr(sinr: Int): Float {
        // Maps -5 to +20 dB to 0 to 1
        return clip((sinr + 5) / 25.0f, 0.0f, 1.0f)
    }
    
    private fun normalizeSpectralEfficiency(cqi: Int, isNR: Boolean): Float {
        val table = if (isNR) nrCqiTable else lteCqiTable
        val maxSE = if (isNR) NR_MAX_SE else LTE_MAX_SE
        
        val se = table[cqi] ?: 0.0
        return (se / maxSE).toFloat()
    }
    
    private fun clip(value: Float, min: Float, max: Float): Float {
        return max(min, min(max, value))
    }
    
    /**
     * Parse bandwidth string to MHz integer
     * Examples: "20", "20MHz", "100 MHz" -> 20, 100
     */
    fun parseBandwidth(bandwidthStr: String?): Int? {
        if (bandwidthStr == null) return null
        
        // Extract numeric part
        val numericPart = bandwidthStr.replace(Regex("[^0-9]"), "")
        return numericPart.toIntOrNull()
    }
}