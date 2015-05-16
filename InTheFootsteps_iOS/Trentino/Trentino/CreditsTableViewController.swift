//
//  CreditsTableViewController.swift
//  Trentino
//
//  Created by Vito Bellini on 08/04/15.
//  Copyright (c) 2015 Vito Bellini. All rights reserved.
//

import UIKit

class CreditsTableViewController: UITableViewController {

    override func viewDidLoad() {
        super.viewDidLoad()

        // Uncomment the following line to preserve selection between presentations
        // self.clearsSelectionOnViewWillAppear = false

        // Uncomment the following line to display an Edit button in the navigation bar for this view controller.
        // self.navigationItem.rightBarButtonItem = self.editButtonItem()
		
		self.navigationItem.title = "Credits"
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }

    // MARK: - Table view data source

    override func numberOfSectionsInTableView(tableView: UITableView) -> Int {
        return 1
    }

    override func tableView(tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return 7
    }

    override func tableView(tableView: UITableView, cellForRowAtIndexPath indexPath: NSIndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCellWithIdentifier("Cell", forIndexPath: indexPath) as! UITableViewCell

        // Configure the cell...
		
		switch(indexPath.row) {
		case 0:
			cell.textLabel?.text = "Paolo Albano"
            cell.detailTextLabel?.text = "<paoloalbano90@gmail.com>"
			
		case 1:
			cell.textLabel?.text = "Leonardo Avena"
            cell.detailTextLabel?.text = "<leonardo.avena88@gmail.com>"
			
		case 2:
			cell.textLabel?.text = "Vito Bellini"
            cell.detailTextLabel?.text = "<v.bellini@gmail.com>"
            
        case 3:
            cell.textLabel?.text = "Gianluca Capodivento"
            cell.detailTextLabel?.text = "<gianluca.capodivento@gmail.com>"
            
        case 4:
            cell.textLabel?.text = "Giuseppe De Santis"
            cell.detailTextLabel?.text = "<giuseppedes90@gmail.com>"
            
        case 5:
            cell.textLabel?.text = "Carmen Manzulli"
            cell.detailTextLabel?.text = "<carmenmanzulli@gmail.com>"
            
        case 6:
            cell.textLabel?.text = "Francesco Marchitelli"
            cell.detailTextLabel?.text = "<marchitelli.francesco@gmail.com>"
			
		default:
			cell.textLabel?.text = nil
            cell.detailTextLabel?.text = nil
		}
        
        return cell
    }


    /*
    // Override to support conditional editing of the table view.
    override func tableView(tableView: UITableView, canEditRowAtIndexPath indexPath: NSIndexPath) -> Bool {
        // Return NO if you do not want the specified item to be editable.
        return true
    }
    */

    /*
    // Override to support editing the table view.
    override func tableView(tableView: UITableView, commitEditingStyle editingStyle: UITableViewCellEditingStyle, forRowAtIndexPath indexPath: NSIndexPath) {
        if editingStyle == .Delete {
            // Delete the row from the data source
            tableView.deleteRowsAtIndexPaths([indexPath], withRowAnimation: .Fade)
        } else if editingStyle == .Insert {
            // Create a new instance of the appropriate class, insert it into the array, and add a new row to the table view
        }    
    }
    */

    /*
    // Override to support rearranging the table view.
    override func tableView(tableView: UITableView, moveRowAtIndexPath fromIndexPath: NSIndexPath, toIndexPath: NSIndexPath) {

    }
    */

    /*
    // Override to support conditional rearranging of the table view.
    override func tableView(tableView: UITableView, canMoveRowAtIndexPath indexPath: NSIndexPath) -> Bool {
        // Return NO if you do not want the item to be re-orderable.
        return true
    }
    */

    /*
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepareForSegue(segue: UIStoryboardSegue, sender: AnyObject?) {
        // Get the new view controller using [segue destinationViewController].
        // Pass the selected object to the new view controller.
    }
    */

}
