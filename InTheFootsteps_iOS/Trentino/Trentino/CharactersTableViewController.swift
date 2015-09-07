//
//  CharactersTableViewController.swift
//  Trentino
//
//  Created by Vito Bellini on 31/03/15.
//  Copyright (c) 2015 Vito Bellini. All rights reserved.
//

import UIKit

import CoreGraphics
import QuartzCore

import Alamofire
import SwiftyJSON

import SwiftSpinner

class CharactersTableViewController: UITableViewController, UISearchResultsUpdating, UISearchBarDelegate {
	var searchController: UISearchController = UISearchController(searchResultsController: nil)
	
	let CELL_HEIGHT: CGFloat = 60

    let endpointCharactersList: String = "http://inthefootsteps.solr.netseven.it/rest/listapersonaggi"
	var charactersList: JSON?
	var filteredList: [JSON] = []
	
	var list: [Character] = []
	
	let sections: [String] = ["#", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"]
	
	var data: Dictionary<String, [Character]> = Dictionary<String, [Character]> ()
	var odata: [(String, Array<Character>)] = []
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
		self.navigationItem.title = "Personaggi storici"

        if let refreshControl = self.refreshControl {
            refreshControl.addTarget(self, action: "refresh", forControlEvents: UIControlEvents.ValueChanged)
        }
		

		self.searchController.searchResultsUpdater = self
		self.searchController.dimsBackgroundDuringPresentation = false
		self.searchController.searchBar.sizeToFit()
		self.searchController.searchBar.delegate = self
		
		self.tableView.tableHeaderView = self.searchController.searchBar;
		
		self.definesPresentationContext = true
		
        Alamofire.Manager.sharedInstance.session.configuration.HTTPAdditionalHeaders = ["Accept": "application/json"]
        
        self.refresh()
    }
    
    func refresh() {
        if let refreshControl = self.refreshControl {
            if(refreshControl.refreshing == false) {
                refreshControl.beginRefreshing()
            }
        }

        SwiftSpinner.show("Carico lista personaggi storici...")

        Alamofire.request(.GET, endpointCharactersList).responseJSON { (request, response, data, error) in
            
            if let err = error {
                println("Error: \(error)")
                println(JSON)
            } else {
				self.charactersList = JSON(data!)
				
				if let list = self.charactersList {
					for (index: String, subJson: JSON) in list {
						if let name = subJson["name"].string {
							let c = Character(name: name, json: subJson)
							
							if let icon = subJson["icona"].string {
								c.icon = icon
							} else {
								println("SUB: \(subJson)")
							}
							
							// add to dictionary
							let firstLetter: String = String(c.name[c.name.startIndex]).uppercaseString

							if let array = self.data[firstLetter] {
								self.data[firstLetter]?.append(c)
							} else {
								self.data[firstLetter] = [Character]()
								self.data[firstLetter]?.append(c)
							}
						}
					}
				}
				self.odata = sorted(self.data) { $0.0 < $1.0 }
				self.tableView.reloadData()
            }
			
            if let refreshControl = self.refreshControl {
                refreshControl.endRefreshing()
            }
            
            SwiftSpinner.hide()
        }
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
	
	// MARK: - Search Controller
	
	func updateSearchResultsForSearchController(searchController: UISearchController) {
		if let list = self.charactersList {
			let searchString: String = self.searchController.searchBar.text
			
			let searchPredicate = NSPredicate(format: "self.name CONTAINS[c] %@", searchController.searchBar.text)

			self.filteredList.removeAll(keepCapacity: false)
			
			for (index: String, subJson: JSON) in list {
				if subJson["name"].stringValue.lowercaseString.rangeOfString(searchString.lowercaseString) != nil {
					self.filteredList.append(subJson)
					
				}
			}
			
			self.tableView.reloadData()
		}
	}
	
	func searchBar(searchBar: UISearchBar, selectedScopeButtonIndexDidChange selectedScope: Int) {
		self.updateSearchResultsForSearchController(self.searchController)
	}

    // MARK: - Table view data source

    override func numberOfSectionsInTableView(tableView: UITableView) -> Int {
		if searchController.active {
			return 1
		} else {
			return self.odata.count
		}
    }
	
	override func sectionIndexTitlesForTableView(tableView: UITableView) -> [AnyObject]! {
		if searchController.active {
			return []
		} else {
			var sectionTitles: [String] = []
			for i in self.odata {
				sectionTitles += [i.0]
			}
			return sectionTitles
		}
	}

	override func tableView(tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
		if searchController.active {
			return self.filteredList.count
		} else {
			return self.odata[section].1.count
		}
	}
	
	override func tableView(tableView: UITableView, sectionForSectionIndexTitle title: String, atIndex index: Int) -> Int {
		if searchController.active {
			return 0
		} else {
			return index
		}
	}
	
	override func tableView(tableView: UITableView, titleForHeaderInSection section: Int) -> String? {
		if searchController.active {
			return nil
		} else {
			return self.odata[section].0
		}
	}
	
	override func tableView(tableView: UITableView, heightForRowAtIndexPath indexPath: NSIndexPath) -> CGFloat {
		return CELL_HEIGHT
	}
	
    override func tableView(tableView: UITableView, cellForRowAtIndexPath indexPath: NSIndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCellWithIdentifier("Cell", forIndexPath: indexPath) as! CharactersTableViewCell

        // Configure the cell...
		
		cell.avatar.image = UIImage(named: "avatar")
		cell.avatar.layer.masksToBounds = true
		cell.avatar.layer.cornerRadius = CGRectGetHeight(cell.avatar.bounds)/2
		
		if searchController.active {
			let p = self.filteredList[indexPath.row]
			cell.nameLabel.text = p["name"].string
			
			if let icon = p["icona"].string {
				println("Filter icon: \(icon)")
				if let iconImage = UIImage(named: icon) {
					cell.avatar.image = iconImage
				} else {
					cell.avatar.image = UIImage(named: "avatar")
				}
			} else {
				cell.avatar.image = UIImage(named: "avatar")
			}
		} else {
			let items: [Character] = self.odata[indexPath.section].1
			let p = items[indexPath.row]
			cell.nameLabel.text = p.name
			
			if let icon = p.icon {
				if let iconImage = UIImage(named: icon) {
					cell.avatar.image = iconImage
				} else {
					cell.avatar.image = UIImage(named: "avatar")
				}
			} else {
				cell.avatar.image = UIImage(named: "avatar")
			}
		}

		return cell
    }

	override func tableView(tableView: UITableView, didSelectRowAtIndexPath indexPath: NSIndexPath) {
		
		if searchController.active {
			let p = self.filteredList[indexPath.row]
			let uri = p["detailEndpoint"].string
			
			self.performSegueWithIdentifier("detail", sender: uri)
		} else {
			let items: [Character] = self.odata[indexPath.section].1
			let p = items[indexPath.row]
			if let uri = p.json["detailEndpoint"].string {
				self.performSegueWithIdentifier("detail", sender: uri)
			}
		}
	}

    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepareForSegue(segue: UIStoryboardSegue, sender: AnyObject?) {
        // Get the new view controller using [segue destinationViewController].
        // Pass the selected object to the new view controller.
		
		if segue.identifier == "detail" {
			let uri = sender as! String
			let detailViewController = segue.destinationViewController as! CharacterDetailViewController
			detailViewController.endpointCharactersDetail = uri
		}
    }

}
