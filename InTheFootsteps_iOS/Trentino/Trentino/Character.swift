//
//  Character.swift
//  Trentino
//
//  Created by Vito Bellini on 14/04/15.
//  Copyright (c) 2015 Vito Bellini. All rights reserved.
//

import Foundation
import SwiftyJSON

class Character {
	var name: String
	var json: JSON
	
	var icon: String?
	
	init(name: String, json: JSON) {
		self.name = name
		self.json = json
	}
}