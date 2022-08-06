//
//  ObservableValue.swift
//  method
//
//  Created by Thipok Cholsaipant on 8/6/22.
//  Copyright © 2022 Thipok Cholsaipant. All rights reserved.
//

import common

public class ObservableValue<T: AnyObject>: ObservableObject {
    private let observableValue: Value
//    private let name : String

    @Published
    var value: T

    private var observer: ((Any) -> Void)?

    init(_ value: Value, _ name: String = "Undefined") {
//        LoggerKt.withTag(tag: "🍎 - ObservableValue").v {"Initializing observation for \(name)..."}
//        self.name = name
        self.observableValue = value
        self.value = (observableValue.value as? T)!
        self.observer = { [weak self] value in
            self?.value = value as! T
        }

        observableValue.subscribe(observer: observer!)
    }

    deinit {
//        LoggerKt.withTag(tag: "🍎 - ObservableValue").v {"Deinitializing observation for \(self.name)..."}
        self.observableValue.unsubscribe(observer: self.observer!)
    }
}
