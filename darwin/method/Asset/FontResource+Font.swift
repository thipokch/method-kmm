//
//  FontResource+Font.swift
//  method
//
//  Created by Thipok Cholsaipant on 8/7/22.
//  Copyright © 2022 Thipok Cholsaipant. All rights reserved.
//

import resource
import SwiftUI

extension FontResource {
    func font(withSize: Double) -> Font {
        return Font(self.uiFont(withSize: withSize))
    }
}
