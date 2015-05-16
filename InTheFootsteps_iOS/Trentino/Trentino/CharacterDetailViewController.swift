//
//  CharacterDetailViewController.swift
//  Trentino
//
//  Created by Vito Bellini on 31/03/15.
//  Copyright (c) 2015 Vito Bellini. All rights reserved.
//

import UIKit
import CoreGraphics
import QuartzCore
import MapKit

import Alamofire
import SwiftyJSON

import SwiftSpinner

//	in-line image with text inside UITextView
//	http://stackoverflow.com/questions/20930462/ios-7-textkit-how-to-insert-images-inline-with-text

class CharacterDetailViewController: UIViewController {
	@IBOutlet var lifeLabel: UILabel!
	@IBOutlet var deathdayLabel: UILabel!
	@IBOutlet var detailTextView: UITextView!
	@IBOutlet var map: MKMapView!
	
	var mapButton: UIBarButtonItem?
	var places: JSON?
	
	var endpointCharactersDetail: String?
	var attributedString: NSAttributedString?
	
    override func viewDidLoad() {
        super.viewDidLoad()

        // Do any additional setup after loading the view.
		
        self.lifeLabel.text = nil
        
		mapButton = UIBarButtonItem(image: UIImage(named: "mapButton"), style: UIBarButtonItemStyle.Done, target: self, action: "mapButton:")
		
		if let endpoint = endpointCharactersDetail {
            SwiftSpinner.show("Recupero dettagli...")
			Alamofire.request(.GET, endpoint).responseJSON { (request, response, data, error) in
				
				if let err = error {
					println("Error: \(error)")
					println(JSON)
				} else {
					let character = JSON(data!)
					
					// Name
					if let characterName = character["name"].string {
						self.navigationItem.title = characterName
					}
					
					// Description
					if let characterDescription = character["description"].string {
						let paragraphStyle = NSMutableParagraphStyle()
						paragraphStyle.alignment = NSTextAlignment.Justified
						paragraphStyle.paragraphSpacing = 10
						
						self.attributedString = NSAttributedString(string: characterDescription, attributes: [NSFontAttributeName: UIFont(name: "Roboto-Light", size: 18.0)!])
						
						if let attributedString = self.attributedString {
							var detailText = NSMutableAttributedString(attributedString: attributedString)
						
							detailText.addAttribute(NSParagraphStyleAttributeName, value: paragraphStyle, range: NSMakeRange(0, detailText.length))

							self.detailTextView.attributedText = detailText
							self.detailTextView.scrollRangeToVisible(NSMakeRange(0, 0))
						}
					}
					
					// Title
					if let characterTitle = character["title"].string {
						self.lifeLabel.text = characterTitle
						self.lifeLabel.adjustsFontSizeToFitWidth = true
					}
					
					// Place
					self.places = character["place"]
					
					if character["place"].arrayValue.count > 0 {
						self.navigationItem.rightBarButtonItem = self.mapButton
					}
					
				}
				
                SwiftSpinner.hide()
			}
		}
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
	
	func mapButton(sender: AnyObject) {
		self.performSegueWithIdentifier("map", sender: nil)
	}

    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepareForSegue(segue: UIStoryboardSegue, sender: AnyObject?) {
        // Get the new view controller using segue.destinationViewController.
        // Pass the selected object to the new view controller.
		if segue.identifier == "map" {
			let mapViewController = segue.destinationViewController as! MapViewController
			mapViewController.places = self.places
		}
    }

}
