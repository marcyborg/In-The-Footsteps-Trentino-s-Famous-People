//
//  CharactersTableViewCell.swift
//  Trentino
//
//  Created by Vito Bellini on 31/03/15.
//  Copyright (c) 2015 Vito Bellini. All rights reserved.
//

import UIKit

class CharactersTableViewCell: UITableViewCell {
	@IBOutlet var avatar: UIImageView!
	@IBOutlet var nameLabel: UILabel!

    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }

    override func setSelected(selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }

}
