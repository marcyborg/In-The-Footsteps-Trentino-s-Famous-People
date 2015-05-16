//
//  HelpViewController.swift
//  Trentino
//
//  Created by Vito Bellini on 15/05/15.
//  Copyright (c) 2015 Vito Bellini. All rights reserved.
//

import UIKit

class HelpViewController: UIViewController {
	
	@IBOutlet var textView: UITextView?

    override func viewDidLoad() {
        super.viewDidLoad()

        // Do any additional setup after loading the view.
		
		self.navigationItem.title = "Help"
		
		if let textView = self.textView {
			textView.scrollRangeToVisible(NSMakeRange(0, 0))
		}
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    

    /*
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepareForSegue(segue: UIStoryboardSegue, sender: AnyObject?) {
        // Get the new view controller using segue.destinationViewController.
        // Pass the selected object to the new view controller.
    }
    */

}
