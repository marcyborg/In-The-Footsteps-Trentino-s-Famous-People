//
//  MapViewController.swift
//  Trentino
//
//  Created by Vito Bellini on 10/04/15.
//  Copyright (c) 2015 Vito Bellini. All rights reserved.
//

import UIKit
import MapKit
import CoreLocation

import Alamofire
import SwiftyJSON

import SwiftSpinner

class MapViewController: UIViewController, MKMapViewDelegate, CLLocationManagerDelegate {
	@IBOutlet var map: MKMapView?
    
    let locationManager: CLLocationManager = CLLocationManager()
	
    var pins: [MKPointAnnotation] = []

	var places: JSON?

    override func viewDidLoad() {
        super.viewDidLoad()
        
        // Do any additional setup after loading the view.
        
        self.navigationItem.title = "Mappa"
        
        locationManager.requestWhenInUseAuthorization()
        locationManager.delegate = self;
        locationManager.distanceFilter = kCLDistanceFilterNone; //whenever we move
        locationManager.desiredAccuracy = kCLLocationAccuracyBest;
        
        if CLLocationManager.locationServicesEnabled() {
            locationManager.startUpdatingLocation()
        }
        
        if CLLocationManager.authorizationStatus() == .NotDetermined {
            locationManager.requestWhenInUseAuthorization()
        }
        
        self.defaultAnnotations()
    }
    
    func defaultAnnotations() {
        if let places = self.places {
            if let items = places.array {
                for p in items {
                    if let title = p["label"].string, lat = p["lat"].double, long = p["long"].double {
                        let location: CLLocationCoordinate2D = CLLocationCoordinate2D(latitude: lat, longitude: long)
                        
                        var pin = MKPointAnnotation()
                        pin.title = title
                        pin.coordinate = location
                        self.pins.append(pin)
                    }
                }
            }
        }
        
        if let map = self.map {
            map.showAnnotations(self.pins, animated: true)
        }
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    // MARK: - Location Manager
    
    func locationManager(manager: CLLocationManager!, didChangeAuthorizationStatus status: CLAuthorizationStatus) {
        if status == CLAuthorizationStatus.AuthorizedAlways || status == .AuthorizedWhenInUse {
            locationManager.startUpdatingLocation()
        }
    }

    // MARK: - Segmented Control
    
    @IBAction func segmentedControlValueChanged(sender: AnyObject) {
        let sc = sender as! UISegmentedControl
        
        if let map = self.map {
            map.removeAnnotations(self.pins)
            self.pins.removeAll(keepCapacity: false)
            
            let centerLocation = map.region.center
            
            let lat = centerLocation.latitude
            let long = centerLocation.longitude
            
            switch(sc.selectedSegmentIndex) {
            case 1:
                // POI
                SwiftSpinner.show("Recupero POI...")
                let endpoint = String(format: "http://inthefootsteps.solr.netseven.it/rest/RicercaLuoghiInteresse/?lat=%f&long=%f&prec=0.01", arguments: [lat, long])
                
                Alamofire.request(.GET, endpoint).responseJSON { (request, response, data, error) in
                    if let err = error {
                        println("Error: \(error)")
                        println(JSON)
                    } else {
                        let poi = JSON(data!)

                        for (index: String, subJson: JSON) in poi {
                            if let title = subJson["label"].string, lat = subJson["lat"].double, long = subJson["long"].double, description = subJson["description"].string {
                                let location: CLLocationCoordinate2D = CLLocationCoordinate2D(latitude: lat, longitude: long)
                                
                                var pin = MKPointAnnotation()
                                pin.title = description
                                pin.subtitle = title
                                pin.coordinate = location
                                
                                self.pins.append(pin)
                            }
                        }
                        
                        map.showAnnotations(self.pins, animated: true)
                    }
                    
                    SwiftSpinner.hide()
                }
                
            case 2:
                // Restaurants
                SwiftSpinner.show("Recupero ristoranti...")
                let endpoint = String(format: "http://inthefootsteps.solr.netseven.it/rest/RicercaRistoranti/?lat=%f&long=%f&prec=0.01", arguments: [lat, long])
                
                Alamofire.request(.GET, endpoint).responseJSON { (request, response, data, error) in
                    
                    if let err = error {
                        println("Error: \(error)")
                        println(JSON)
                    } else {
                        let restaurants = JSON(data!)
                        
                        for (index: String, subJson: JSON) in restaurants {
                            if let title = subJson["name"].string, lat = subJson["lat"].double, long = subJson["long"].double {
                                let location: CLLocationCoordinate2D = CLLocationCoordinate2D(latitude: lat, longitude: long)
                                
                                var pin = MKPointAnnotation()
                                pin.title = title
                                pin.coordinate = location
                                
                                if let phone = subJson["phone"].string {
                                    pin.subtitle = phone
                                }
                                
                                self.pins.append(pin)
                            }
                        }
                        
                        map.showAnnotations(self.pins, animated: true)
                    }
                    
                    SwiftSpinner.hide()
                }
                
            case 3:
                // Hotels
                SwiftSpinner.show("Recupero strutture ricettive...")
                let endpoint = String(format: "http://inthefootsteps.solr.netseven.it/rest/RicercaHotel/?lat=%f&long=%f&prec=0.01", arguments: [lat, long])
                
                Alamofire.request(.GET, endpoint).responseJSON { (request, response, data, error) in
                    
                    if let err = error {
                        println("Error: \(error)")
                        println(JSON)
                    } else {
                        let hotels = JSON(data!)
                        println(hotels)
                        for (index: String, subJson: JSON) in hotels {
                            if let title = subJson["name"].string, lat = subJson["lat"].double, long = subJson["long"].double {
                                let location: CLLocationCoordinate2D = CLLocationCoordinate2D(latitude: lat, longitude: long)

                                var pin = MKPointAnnotation()
                                pin.title = title
                                pin.coordinate = location
                                
                                if let phone = subJson["phone"].string {
                                    pin.subtitle = phone
                                }
                                
                                self.pins.append(pin)
                            }
                        }
                        
                        map.showAnnotations(self.pins, animated: true)
                    }
                    
                    SwiftSpinner.hide()
                }
                
            default:
                self.defaultAnnotations()
            }
        }
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
