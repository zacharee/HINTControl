//
//  HINT_Widget.swift
//  HINT Widget
//
//  Created by Zachary Wander on 7/13/23.
//  Copyright © 2023 orgName. All rights reserved.
//

import AppIntents
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
                
                let nextUpdate = Calendar.current.date(byAdding: .second, value: SettingsModel.shared.widgetRefresh.value?.intValue ?? 60, to: date)
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
        VStack {
            HStack {
                Text(MR.strings.shared.connection.desc().localized())
                
                Spacer()
                
                if #available(iOS 17.0, *) {
                    Button(
                        intent: RefreshIntent()
                    ) {
                        Image(uiImage: MR.images.shared.refresh.toUIImage()!)
                            .renderingMode(.template)
                            .foregroundColor(Color(UIColor.label))
                    }.buttonStyle(.borderless)
                }
            }
            
            LazyVStack(spacing: 8) {
                HINT_WidgetEntryItem(data: entry.signalData?.fourG, advancedData: entry.cellData?.cell?.fourG)
                HINT_WidgetEntryItem(data: entry.signalData?.fiveG, advancedData: entry.cellData?.cell?.fiveG)
            }.frame(maxWidth: .infinity, maxHeight: .infinity)
                .padding(.all, 8)
        }.frame(maxWidth: .infinity, maxHeight: .infinity)
    }
}

extension RoundedRectangle {
    func fillCompat() -> RoundedRectangle {
        if #available(iOS 17.0, *) {
            fill(.fill.secondary)
        } else if #available(iOS 15.0, *) {
            fill(.secondary)
        } else {
            fill(Color.gray)
        }
        
        return self
    }
}

struct HINT_WidgetEntryItem : View {
    var data: BaseCellData?
    var advancedData: BaseAdvancedData?
    
    var body: some View {
        ZStack(alignment: Alignment(horizontal: .center, vertical: .center)) {
            RoundedRectangle(cornerSize: CGSize(width: 8, height: 8))
                .fillCompat()
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
    @Environment(\.colorScheme) var colorScheme
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
            } else if #available(iOS 15.0, *) {
                HINT_WidgetEntryView(entry: entry)
                    .padding()
                    .background()
            } else {
                HINT_WidgetEntryView(entry: entry)
                    .padding()
                    .background(colorScheme == .dark ? Color.black : Color.white)
            }
        }.supportedFamilies([.systemMedium])
    }
}

@available(iOS 16.0, macOS 13.0, watchOS 9.0, tvOS 16.0, *)
struct RefreshIntent : AppIntent {
    static var title: LocalizedStringResource = LocalizedStringResource(stringLiteral: MR.strings.shared.refresh.desc().localized())
    static var description: IntentDescription? = nil
    
    func perform() async throws -> some IntentResult {
        WidgetCenter.shared.reloadTimelines(ofKind: "HINT_Widget")
        return .result()
    }
}

#Preview(as: .systemSmall) {
    HINT_Widget()
} timeline: {
    SimpleEntry(date: Date(), cellData: nil, signalData: nil)
    SimpleEntry(date: Date(), cellData: nil, signalData: nil)
}
