//
//  ComponentHolder.swift
//  method
//
//  Created by Thipok Cholsaipant on 8/6/22.
//  Copyright © 2022 Thipok Cholsaipant. All rights reserved.
//

import common

class ComponentHolder<T> {
    let lifecycle: LifecycleRegistry
    let component: T

    init(factory: (ComponentContext) -> T) {
        let lifecycle = LifecycleRegistryKt.LifecycleRegistry()
        let component = factory(DefaultComponentContext(lifecycle: lifecycle))
        self.lifecycle = lifecycle
        self.component = component

        lifecycle.onCreate()
    }

    deinit {
        lifecycle.onDestroy()
    }
}
