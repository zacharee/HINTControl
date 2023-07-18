//
//  HINT_Widget.swift
//  HINT Widget
//
//  Created by Zachary Wander on 7/13/23.
//  Copyright © 2023 orgName. All rights reserved.
//

import Bugsnag
import WidgetKit
import SwiftUI
import common

struct Provider: TimelineProvider {
    func placeholder(in context: Context) -> SimpleEntry {
        let cellData = MainModel.shared.currentCellData.value
        let signalData = MainModel.shared.currentMainData.value?.signal
        
        return SimpleEntry(
            date: Date(),
            cellData: cellData,
            signalData: signalData
        )
    }

    func getSnapshot(in context: Context, completion: @escaping (SimpleEntry) -> ()) {
        Task {
            do {
                let httpClient = try await GlobalModel.shared.updateClient()
                try await httpClient?.logIn(username: UserModel.shared.username.value as? String ?? "", password: UserModel.shared.password.value as? String ?? "", rememberCredentials: true)
                let cellData = try await httpClient?.getCellData()
                let signalData = try await httpClient?.getMainData(unauthed: false)?.signal
                
                let entry = SimpleEntry(
                    date: Date(),
                    cellData: cellData,
                    signalData: signalData
                )
                completion(entry)
            } catch {
                print("Error getting snapshot: \(error)")
                Bugsnag.notifyError(error)
            }
        }
    }

    func getTimeline(in context: Context, completion: @escaping (Timeline<Entry>) -> ()) {
        Task {
            do {
                let httpClient = try await GlobalModel.shared.updateClient()
                try await httpClient?.logIn(username: UserModel.shared.username.value as? String ?? "", password: UserModel.shared.password.value as? String ?? "", rememberCredentials: true)
                            
                let cellData = try await httpClient?.getCellData()
                let signalData = try await httpClient?.getMainData(unauthed: false)?.signal
                
                let date = Date()
                let entry = SimpleEntry(date: date, cellData: cellData, signalData: signalData)
                
                let nextUpdate = Calendar.current.date(byAdding: .second, value: 1, to: date)
                let timeline = Timeline(
                    entries: [entry],
                    policy: .after(nextUpdate ?? date)
                )
                
                completion(timeline)
            } catch {
                print("Error getting timeline: \(error)")
                Bugsnag.notifyError(error)
            }
        }
    }
}

struct SimpleEntry: TimelineEntry {
    var date: Date
    let cellData: CellDataRoot?
    let signalData: SignalData?
}

struct HINT_WidgetEntryView : View {
    var entry: Provider.Entry

    var body: some View {
        LazyVStack(spacing: 8) {
            HINT_WidgetEntryItem(data: entry.signalData?.fourG, advancedData: entry.cellData?.cell?.fourG)
            HINT_WidgetEntryItem(data: entry.signalData?.fiveG, advancedData: entry.cellData?.cell?.fiveG)
        }.frame(maxWidth: .infinity, maxHeight: .infinity)
            .padding(.all, 8)
    }
}

struct HINT_WidgetEntryItem : View {
    var data: BaseCellData?
    var advancedData: BaseAdvancedData?
    
    var body: some View {
        ZStack(alignment: Alignment(horizontal: .center, vertical: .center)) {
            RoundedRectangle(cornerSize: CGSize(width: 8, height: 8))
                .fill(.secondary)
                .frame(maxWidth: .infinity, minHeight: 32)
            
            HStack(alignment: .center, spacing: 8) {
                if (data != nil || advancedData != nil) {
                    Spacer()
                    if let bands = data?.bands {
                        TwoRowText(
                            label: MR.strings.shared.bands.desc().localized(),
                            value: bands.joined(separator: ", ")
                        )
                    }
                    
                    if let rsrp = data?.rsrp {
                        Spacer()
                        TwoRowText(
                            label: MR.strings.shared.rsrp.desc().localized(),
                            value: "\(rsrp)"
                        )
                    }
                    
                    if let rsrq = data?.rsrq {
                        Spacer()
                        TwoRowText(
                            label: MR.strings.shared.rsrq.desc().localized(),
                            value: "\(rsrq)"
                        )
                    }
                    
                    if let bandwidth = advancedData?.bandwidth {
                        Spacer()
                        TwoRowText(
                            label: MR.strings.shared.bandwidth.desc().localized(),
                            value: "\(bandwidth)"
                        )
                    }
                    Spacer()
                } else {
                    Text(verbatim: MR.strings.shared.not_connected.desc().localized())
                }
            }.frame(maxWidth: .infinity, maxHeight: .infinity)
        }.frame(maxWidth: .infinity, maxHeight: .infinity)
    }
}

struct TwoRowText : View {
    var label: String
    var value: String
    
    var body: some View {
        VStack(alignment: .center) {
            Text(value)
                .multilineTextAlignment(.center)
            Text(label)
                .multilineTextAlignment(.center)
                .lineLimit(1)
                .font(.caption)
        }
    }
}

struct HINT_Widget: Widget {
    let kind: String = "HINT_Widget"
    
    init() {
        let config = BugsnagConfiguration.loadConfig()
        
        config.addOnSendError { event in
            CrossPlatformBugsnag.shared.generateExtraErrorData().forEach { data in
                event.addMetadata(data.value, key: data.key, section: data.tabName)
            }
            return true
        }
        
        Main_iosKt.updateBugsnagConfig(config: config)
        Bugsnag.start()
        Main_iosKt.setupBugsnag()
    }

    var body: some WidgetConfiguration {
        StaticConfiguration(kind: kind, provider: Provider()) { entry in
            if #available(iOS 17.0, *) {
                HINT_WidgetEntryView(entry: entry)
                    .containerBackground(.fill.tertiary, for: .widget)
            } else {
                HINT_WidgetEntryView(entry: entry)
                    .padding()
                    .background()
            }
        }.supportedFamilies([.systemMedium])
    }
}

#Preview(as: .systemSmall) {
    HINT_Widget()
} timeline: {
    SimpleEntry(date: Date(), cellData: nil, signalData: nil)
    SimpleEntry(date: Date(), cellData: nil, signalData: nil)
}
