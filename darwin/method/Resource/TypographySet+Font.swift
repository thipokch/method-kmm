//
//  TypographySet+Font.swift
//  method
//
//  Created by Thipok Cholsaipant on 8/8/22.
//  Copyright © 2022 Thipok Cholsaipant. All rights reserved.
//

import resource
import SwiftUI

extension TypographySet {
    var swiftFont : Font {
        get {
            return Font(
                self.font.uiFont(withSize: self.size)
            )
        }
    }
}
