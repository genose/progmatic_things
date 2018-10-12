
       /* *********************************************** */
       /* *********************************************** */
       /* *********************************************** */
       /* *********************************************** */
         String.prototype.escapeRegExp  = function (astring){
            return astring.replace(/[.*+?^${}()|[\]\\]/g, "\\$&");
        };
         
        /* Define functin to find and replace specified term with replacement string */
        String.prototype.replaceAll = function (term, replacement) {
          return this.replace(new RegExp(this.escapeRegExp(term), 'g'), replacement);
        };
        
        /*
            var showBtn = document.querySelector('.show');
alert('doc '+document.getElementById('show')+'::'+browser.downloads);
            (showBtn || document.getElementById('show')).onclick = function() {
                alert('try to open defaul folder ...');
                try{
                    browser.downloads.showDefaultFolder();    
                }catch(ev_err_apierror){
                    console.log('Error API :: '+ev_err_apierror);
                }
                alert('Done ...');
            }*/
        
       /* *********************************************** */
       /* *********************************************** */
       // https://www.bluetooth.com/specifications/gatt/characteristics
       // https://googlechrome.github.io/samples/web-bluetooth/automatic-reconnect.html
       // https://webbluetoothcg.github.io/web-bluetooth/tests#heartratedevice
       // https://github.com/stefanpenner/es6-promise
       // https://developer.mozilla.org/en-US/docs/Web/API/BluetoothRemoteGATTServer
       
       /* *********************************************** */
       /* *********************************************** */
       function BLEGATTAPIVersionImplementationWarning(aExplainTextImplementation, aObjectPathImplementation, aImplementationValue, outputConsoleImplementation ){
              var aLigneSeparator = '*******************************************************************';
              var aExplainedImplementationError = '\r\n'+aLigneSeparator+'\r\n'+aLigneSeparator+'\r\n'+' !!!! .... Cant do that on this object with this API Version (October 2018) ... !!!! \r\n'+aLigneSeparator+'\r\n >>>> TODO :: Implementation Version when stable for '+aExplainTextImplementation+' thru ['+aObjectPathImplementation+'=['+aImplementationValue+']])\r\n'+aLigneSeparator+'\r\n'+aLigneSeparator+'\r\n'+'\r\n';
              if(outputConsoleImplementation)
              {
                     console.log(Error(''+aExplainedImplementationError+''));
              }
              
              return aExplainedImplementationError;
       }
       
	var BLEGattConnectDevice = function(){ alert('ERROR::BLEGattConnectDevice(Not initilised Web Bluetooth support or Unsupported configuration ...)');};
        try{
            
            
            
            var matchChrome = navigator.userAgent.match(/Chrome\/([0-9]+)\./);
            var verChrome = matchChrome ? parseInt(matchChrome[1]) : 0;
            var ChromeVersionCompatMin =  ( ( (parseInt(verChrome)+1) >=70)?(parseInt(verChrome) +1 ): 0) || 70 ; // 2018 Beta
            var ChromeVersion = (parseInt(verChrome) || ( ChromeVersionCompatMin -1) ); // :: version since 08/October/2018
            
            var matchGecko = navigator.userAgent.match(/Firefox\/([0-9]+)\./);
            var verGecko = matchGecko ? parseInt(matchGecko[1]) : 0;
            var GeckoVersionCompatBLEMin = ( ( (parseInt(verGecko)+1) >=72)?(parseInt(verGecko) +1 ): 0) || 72; // 2019 Nightly
            var GeckoVersion = (parseInt(verGecko) || (GeckoVersionCompatBLEMin -1) );
            
         
            var matchEdge = navigator.userAgent.match(/Edge\/([0-9]+)\./);
            var verEdge = matchEdge ? parseInt(matchEdge[1]) : 0;
            var EdgeVersionCompatBLEMin = ( ( (parseInt(verEdge)+1) >=22)?(parseInt(verEdge) +1 ): 0) || 22; // 2020 Edge
            var EdgeVersion = (parseInt(verEdge) || (EdgeVersionCompatBLEMin - 1) );
            
            
            var matchSafari = navigator.userAgent.match(/Safari\/([0-9]+)\./);
            var matchMacOS = navigator.userAgent.match(/Mac/);
            var verSafari = matchSafari ? parseInt(matchSafari[1]) : 0;
            var SafariVersionCompatBLEMin = ( ( (parseInt(verSafari)+1) >=570)?(parseInt(verSafari) +1 ): 0) || 570;
            var SafariVersion = (parseInt(verSafari) || (SafariVersionCompatBLEMin -1));
            
            var matchSystem = navigator.userAgent.match(/\((Win([a-z|A-Z|\ ]+))([0-9|\.]+)\;/) || navigator.userAgent.match(/\((Mac([a-z|A-Z|\ ]+))\;([a-z|A-Z|\ ]+)10\_([0-9|\_]+)\;/);
            navigator.userSystem = (((matchSystem) ? (matchSystem[1]||'Mac ').trim(): null) || 'Mac') .toLowerCase();
            navigator.userSystemVersion = ((matchSystem && matchSystem[3]) ? parseFloat(matchSystem[3].replaceAll('_','.')): null) || ((matchSystem && matchSystem[4])? parseFloat(matchSystem[4].replaceAll('_','.')):0) || 0;
            navigator.userAgentIsEdge = false;
			navigator.userAgentIsChrome = false;
			navigator.userAgentIsSafari = false;
			navigator.userAgentIsGecko = false;
            
            
            if(matchEdge){
                navigator.appNameReal = 'Edge';
                navigator.appVersionReal = EdgeVersion;
                navigator.appVersionRealMinCompat = EdgeVersionCompatBLEMin;
                
                navigator.userAgentIsEdge = true;
                
            }else if(matchChrome){
                navigator.appNameReal = 'Chrome';
                navigator.appVersionReal = ChromeVersion;
                navigator.appVersionRealMinCompat = ChromeVersionCompatMin;
                
                navigator.userAgentIsChrome = true;
                
            }else if(matchSafari && matchMacOS){
                navigator.appNameReal = 'Safari';
                navigator.appVersionReal = SafariVersion;
                navigator.appVersionRealMinCompat = SafariVersionCompatBLEMin;
                
                navigator.userAgentIsSafari = true;
                
            }else if(matchGecko){
                navigator.appNameReal = 'Firefox';
                navigator.appVersionReal = GeckoVersion;
                navigator.appVersionRealMinCompat = GeckoVersionCompatBLEMin;
                
                navigator.userAgentIsGecko = true;
                
            }else{
                navigator.appNameReal = navigator.appCodeName;
                navigator.appVersionReal = navigator.appVersion;
                navigator.appVersionRealMinCompat = (parseInt(navigator.appVersion) || 0) +1;
                throw new Error (navigator.appNameReal+" Found With No Warranties");
            }
            
            console.log('navigator.userAgent:'+navigator.userAgent);
            console.log('navigator.userSystem:'+ navigator.userSystem+'::navigator.userSystemVersion:'+ navigator.userSystemVersion);
            
            console.log('navigator.appNameReal:'+navigator.appNameReal+'::navigator.appVersionReal:'+navigator.appVersionReal+'::navigator.appVersionRealMinCompat:'+navigator.appVersionRealMinCompat);
            
           
        }catch(ev_err_navigator_boottests){
            console.log('Error::ev_err_navigator_boottests('+ev_err_navigator_boottests+')');
            alert('Error::ev_err_navigator_boottests('+ev_err_navigator_boottests+')');
        }
       /* *********************************************** */
       /* *********************************************** */
       /* *********************************************** */
       /* *********************************************** */
       var BLEGATTServerHandle = null;
       const BLEGATTServerHandleRetryReconnectMax = 3;
       var BLEGATTServerHandleRetryReconnect = 0;
       var BLEGATTDiscoverTimeOutHandle = null;
       var BLEGATTDeviceSelected = null;
       var BLEGATTDevice_userHadSelectedDevice = false;
        
        
        var BLEGATT_resolvedServicesCount = 0;
        var BLEGATT_resolvedServicesArray = [];
       /* *********************************************** */
       /* *********************************************** */
       /* *********************************************** */
       /* *********************************************** */
	   var aDocNodeElement_BLEGATTCheckSupportMessage = null;
	   var aDocNodeElement_bluetooth_progressbar = null;
	   var aDocNodeElement_bluetooth_message = null;
	   var aDocNodeElement_bluetooth_btn_findDevices = null;
       /* *********************************************** */
       /* *********************************************** */
       /* *********************************************** */
       /* *********************************************** */
        // :: filters: [{ services: ['battery_service'] }]
                let BLEGATTServicesValid = [
                                            
                                            'generic_attribute',
                                            'generic_access',
                                            'current_time',
                                            
                                            'device_information',
                                            'battery_service',
                                            
                                            'object_transfer',
                                            'scan_parameters',
                                            
                                            'user_data',
                                            
                                            'pulse_oximeter',
                                            
                                            'heart_rate',
                                            
                                            'blood_pressure',
                                            
                                            'health_thermometer',
                                            
                                            'body_composition',
                                            
                                            // 'insulin_delivery',
                                            
                                            'glucose',
                                            'continuous_glucose_monitoring',
                                            
                                            'weight_scale',
                                            
                                            'reconnection_configuration',
                                            'reference_time_update',
                                            'running_speed_and_cadence',
                                            
                                            
                                           
                                            // LabPad INR
                                           '2c1b2460-4a5f-11e5-9595-0002a5d5c51b',
                                           
                                            // Thomson CheckMe
                                           '14839ac4-7d7e-415c-9a42-167340cf2339',
                                           '00008a81-7d7e-415c-9a42-167340cf2339',
                                            
                                           // Generic GATT Services .... 
                                            '00001810-0000-1000-8000-00805f9b34fb', //  ... :: GenericAccess ;; DeviceInformation ;; [...], DeviceName 
                                            '0000181a-0000-1000-8000-00805f9b34fb', // ...  :: DeviceInformation ;; Misc
                                            '0000181c-0000-1000-8000-00805f9b34fb', // ... :: USERData
                                            '0000181f-0000-1000-8000-00805f9b34fb', // ... :: Battery
                                            
                                            
                                            
                                            
                                            ];
                
               
       /* *********************************************** */
       /* *********************************************** */ 
              let BLEGATTCharacteristicsValid = {
                                                  
                                                  'misc': {
                                                              'battery':[
                                                                      'battery_level',
                                                                      'battery_level_state',
                                                                      'battery_power_state'
                                                                      ],
                                                              'misc_device_information':{
                                                                  'firmware_revision_string': 'firmware_revision_string',
                                                                  'hardware_revision_string': 'hardware_revision_string',
                                                                  'manufacturer_name_string': 'manufacturer_name_string',
                                                                  'model_number_string': 'model_number_string',
                                                                  'serial_number_string' : 'serial_number_string',
                                                                  'software_revision_string': 'software_revision_string'
                                                              },
                                                              'misc_device_sensor':{
                                                                  'measurement_interval' : 'measurement_interval',
                                                                  'sensor_location' : 'sensor_location',                                                               
                                                                  'supported_heart_rate_range':'supported_heart_rate_range',
                                                                  'supported_speed_range':'supported_speed_range'
                                                              },
                                                              'misc_object':{
                                                                  'object_action_control_point' : 'object_action_control_point',
                                                                  'object_changed' : 'object_changed',
                                                                  'object_first_created' : 'object_first_created',
                                                                  'object_id' : 'object_id',
                                                                  'object_last_modified' : 'object_last_modified',
                                                                  'object_list_control_point' : 'object_list_control_point',
                                                                  'object_list_filter' : 'object_list_filter',
                                                                  'object_name' : 'object_name',
                                                                  'object_properties' : 'object_properties',
                                                                  'object_size' : 'object_size',
                                                                  'object_type' : 'object_type',
                                                                  'ots_feature' : 'ots_feature',
                                                                  
                                                                  'sc_control_point' : 'sc_control_point'
                                                              }



                                                  },
                                                  'body_misc':{
                                                              
                                                              'pressure' : 'pressure'
                                                  },
                                                  'body_temperature':{
                                                              'temperature' : 'temperature',
                                                              'temperature_type' : 'temperature_type',
                                                              'temperature_measurement' : 'temperature_measurement',
                                                              'temperature_celsius' : 'temperature_celsius',
                                                              'temperature_fahrenheit' : 'temperature_fahrenheit'
                                                  },
                                                  
                                                  'blood': {
                                                              'blood_misc_pressure':{
                                                                  'blood_pressure_feature' : 'blood_pressure_feature',
                                                                  'blood_pressure_measurement' : 'blood_pressure_measurement',
                                                              },
                                                              'blood_misc_composition':{
                                                              'body_composition_feature' : 'body_composition_feature',
                                                              'body_composition_measurement' : 'body_composition_measurement',
                                                              },
                                                              'blood_misc_glucose':{
                                                                  'glucose_feature' : 'glucose_feature',
                                                                  'glucose_measurement' : 'glucose_measurement',
                                                                  'glucose_measurement_context' : 'glucose_measurement_context',
                                                                  'gust_factor' : 'gust_factor'
                                                              }

                                                  },
                                                  'heart': {
                                                              
                                                              'heart_misc_sport':{
                                                                      
                                                                  'indoor_bike_data' : 'indoor_bike_data',
                                                                  'indoor_positioning_configuration' : 'indoor_positioning_configuration',

                                                                  'rsc_feature' : 'rsc_feature', // :: Running Speed Cadence
                                                                  'rsc_measurement' : 'rsc_measurement'
                                                              },
                                                              
                                                              'heart_misc':{
                                                                  'heart_rate_control_point' : 'heart_rate_control_point',
                                                                  'heart_rate_max' : 'heart_rate_max',
                                                                  'heart_rate_measurement' : 'heart_rate_measurement',
                                                                  'maximum_recommended_heart_rate' : 'maximum_recommended_heart_rate',
                                                                  'resting_heart_rate' : 'resting_heart_rate',
                                                                  

                                                                  'aerobic_heart_rate_lower_limit' : 'aerobic_heart_rate_lower_limit',
                                                                  'aerobic_heart_rate_upper_limit' : 'aerobic_heart_rate_upper_limit',
                                                                  'aerobic_threshold' : 'aerobic_threshold',
                                                                  'anaerobic_heart_rate_lower_limit' : 'anaerobic_heart_rate_lower_limit',
                                                                  'anaerobic_heart_rate_upper_limit' : 'anaerobic_heart_rate_upper_limit',
                                                              },
                                                              'heart_misc_fat':{ 
                                                                  'fat_burn_heart_rate_lower_limit' : 'fat_burn_heart_rate_lower_limit',
                                                                  'fat_burn_heart_rate_upper_limit' : 'fat_burn_heart_rate_upper_limit'
                                                              },
                                                              
                                                  },
                                                    
                                                    
                
                };
                
                
       /* *********************************************** */
       /* *********************************************** */
              let BLEGATTServicesValidAssociated_DevicesCharacteristics = {
                                          'generic_attribute':[],
                                          'generic_access':[],
                                          'device_information' : BLEGATTCharacteristicsValid.misc,
                                          'battery_service':BLEGATTCharacteristicsValid.misc.battery,
                                          'object_transfer':BLEGATTCharacteristicsValid.misc.misc_object,
                                          'scan_parameters':BLEGATTCharacteristicsValid.misc.misc_device_sensor,
                                          'user_data':[],
                                          'pulse_oximeter':[],
                                          'heart_rate':BLEGATTCharacteristicsValid.heart,
                                          'health_thermometer':BLEGATTCharacteristicsValid.body_temperature,
                                          'blood_pressure':BLEGATTCharacteristicsValid.blood.blood_misc_pressure,
                                          'glucose':BLEGATTCharacteristicsValid.blood.blood_misc_glucose,
                                          'weight_scale':[],
                                          '14839ac4-7d7e-415c-9a42-167340cf2339':    [ // Thomson CheckMe
                                                                                           
                                                                                           '0734594a-a8e7-4b1a-a6b1-cd5243059a57',
                                                                                           'ba04c4b2-892b-43be-b69c-5d13f2195392',
                                                                                           '8b00ace7-eb0b-49b0-bbe9-9aee0a26e1a3',
                                                                                           'e06d5efb-4f4a-45c0-9eb1-371ae5a14ad4',
                                                                                           'PeripheralPreferredConnectionParameters'
                                                                                    ],
                                          '2c1b2460-4a5f-11e5-9595-0002a5d5c51b' : [ // LabPad
                                                                                       '2c1b2462-4a5f-11e5-9595-0002a5d5c51b',
                                                                                       '2c1b2463-4a5f-11e5-9595-0002a5d5c51b',
                                                                                       '2c1b2464-4a5f-11e5-9595-0002a5d5c51b',
                                                                                       '2c1b2466-4a5f-11e5-9595-0002a5d5c51b'
                                                                                    ]
                                          };
       /* *********************************************** */
       /* *********************************************** */
       let BLEGATTServicesValidAssociated_DevicesCharacteristics_ReadedValue = {};
       /* *********************************************** */
       /* *********************************************** */
       /* *********************************************** */
       /* *********************************************** */
       
		function BLEGATTSetUIProgress(message, progress_value){
		  
			try{
				if(aDocNodeElement_bluetooth_progressbar .setAttribute && aDocNodeElement_bluetooth_progressbar .getAttribute('aria-valuenow')){
					console.log('BLEGATTSetUIProgress (aria-valuenow) :: '+aDocNodeElement_bluetooth_progressbar.style+' :: '+aDocNodeElement_bluetooth_progressbar.style.width); 
					aDocNodeElement_bluetooth_progressbar .setAttribute('aria-valuenow',( parseInt(progress_value) || 0) );
				}else if(aDocNodeElement_bluetooth_progressbar .style){
					// console.log('BLEGATTSetUIProgress (style) :: '+aDocNodeElement_bluetooth_progressbar.style+' :: '+aDocNodeElement_bluetooth_progressbar.style.width+' ::elm '+aDocNodeElement_bluetooth_progressbar.style.maxwidth+' ::parent '+aDocNodeElement_bluetooth_progressbar.parentNode.style.width);
					var aElement_maxwidth  = (aDocNodeElement_bluetooth_progressbar.style.maxwidth || aDocNodeElement_bluetooth_progressbar.parentNode.style.maxwidth || aDocNodeElement_bluetooth_progressbar.parentNode.style.width || aDocNodeElement_bluetooth_progressbar.parentNode.parentNode.style.width || aDocNodeElement_bluetooth_progressbar.parentNode.parentNode.style.maxwidth || 490);
					
					var aElement_maxwidth_portion  =(parseInt(aElement_maxwidth) / 100);
					
					aDocNodeElement_bluetooth_progressbar .style.width = ( (parseInt(progress_value) * aElement_maxwidth_portion ) || 0) +'px' ;
					aDocNodeElement_bluetooth_progressbar.innerHTML = (' '+(parseInt(progress_value)||10)) +' %';
				}else{
					throw new Error('DOM Implementation Error');
				}
			   
			}catch(ev_err_node_bluetoothUI_NOTFOUND){
			   console.log(new Error('::bluetooth_progressbar::'+ev_err_node_bluetoothUI_NOTFOUND));
			}
			
			try{
				aDocNodeElement_bluetooth_message.innerHTML = ( message || 'Veuillez patienter ...');
				
			}catch(ev_err_node_bluetoothUI_NOTFOUND){
			   console.log(new Error('::bluetooth_message::'+ev_err_node_bluetoothUI_NOTFOUND));
			}
		   
		}
       
       
       /* *********************************************** */
       /* *********************************************** */
       /* *********************************************** */
       /* *********************************************** */
        function takeanap_sleep(ms) {
            return new Promise(resolve => setTimeout(resolve, ms));
        }

       /* *********************************************** */
       /* *********************************************** */
       /* *********************************************** */
       /* *********************************************** */
       
       async function asyncResolvingService(BLEServerPTR, serviceIterator){
                        
            // var aServiceGatt = null;
              try{
                     console.log('Try discover ... ('+BLEServerPTR+'::'+serviceIterator+')');
                 
                     BLEGATTDiscoverTimeOutHandle = setTimeout(10000, function(){
                            console.log('Exception : TimeOut BLEGattServer is unavaible ...');
                            throw Error('Exception : TimeOut BLEGattServer is unavaible ...');
                     });
                               
              return   Promise.all([ BLEGATTServerHandle.getPrimaryServices(serviceIterator)]).then(service=>{
                     try {
                            
                            console.log('>>>> Try getting Service ['+service+']'); 
                            if(!service) {
                                console.log('>>> Service is Null ');    
                                return null;
                            }
                           
                            // **************************************************
                            // **************************************************
                            
                            // ***********************************************
                            // :: Different implementation
                            // ******* To List some services Characteristics
                            // ***********************************************
                            if(!service.getCharacteristic)
                            {
                                   // TODO :: Implementation Stable Version ...    
                               BLEGATTAPIVersionImplementationWarning('Gathering some Service Values','(BluetoothRemoteGATTService).getCharacteristic', (service).getCharacteristic, true );
                               
                               return Promise.all([ service ]);
                            }
                            // ***********************************************
                            // :: Different implementation
                            // ******* To List all services Characteristics
                            // ***********************************************
                            if(!aGAttserviceDescriptor.getCharacteristics)
                            {
                                   BLEGATTAPIVersionImplementationWarning('Gathering Multiple Services Values','(BluetoothRemoteGATTService).getCharacteristicS', (service).getCharacteristics, true );
                            }
                            
                            // **************************************************
                            // **************************************************
                            // Getting [Some] Characteristics ...
                            
                            // **************************************************
                            // **************************************************
                            console.log('>>>> Gattering Service Characteristic... ['+(service||[]).uuid+'::'+(service||[]).getCharacteristic+'::'+(service||[]).getCharacteristics+'::'+(service||[]).getIncludedServices+']');
                          
                            // **************************************************
                            // **************************************************
                            if(!BLEGATTServicesValidAssociated_DevicesCharacteristics[serviceIterator] || (BLEGATTServicesValidAssociated_DevicesCharacteristics[serviceIterator].length = 0))
                            {
                                 console.log(' >>>>>> info :: fetcheable definition is Empty for  ['+serviceIterator+']');
                                 return null;
                            }
                            console.log(' >>>>>> Service Read for  ['+serviceIterator+'] shall get characteristics : ['+service.getCharacteristic(BLEGATTServicesValidAssociated_DevicesCharacteristics[serviceIterator][0])+']');
                            //:: BLEGATTCharacteristicsValid
                            
                            BLEGATTServicesValidAssociated_DevicesCharacteristics_ReadedValue[serviceIterator] = {};
                            
                             for( var charateristicIterator in BLEGATTServicesValidAssociated_DevicesCharacteristics[serviceIterator])
                             {
                                   try{
                                          BLEGATTServicesValidAssociated_DevicesCharacteristics_ReadedValue[serviceIterator][charateristicIterator] =  service.getCharacteristic(BLEGATTServicesValidAssociated_DevicesCharacteristics[serviceIterator][ charateristicIterator ]).then(characteristic => {
                                   
                                                 if(!characteristic){
                                                     console.log('>>>> characteristic is Null ');    
                                                     return null;
                                                 }
                                                 
                                                 // Reading Battery Level...
                                                 console.log('>>>> Getting Characteristic Value ... ['+serviceIterator+'@'+charateristicIterator+']');
                                                 return Promise.all([characteristic.readValue()]);
                                          });
                                    }catch(ev_err_getservice_char_iterator){
                                          console.log(Error(' ev_err_getservice_char_iterator :: '+ev_err_getservice_char_iterator));
                                   }
                            };
                            return Promise.all([ BLEGATTServicesValidAssociated_DevicesCharacteristics_ReadedValue ]);
                          /* 
                        */
                     }catch(ev_err_getservice_char){
                            console.log(Error(' ev_err_getservice_char :: '+ev_err_getservice_char));
                     }
                     return null;
                }).catch(ev_err_discovering=>{
                    // ::  console.log('Disvecorying Error ... '+serviceIterator+'>>'+ev_err_discovering);
                });
                // return BLEServerPTRT;
                
            }catch(ev_err_services_enumeration){
                console.log((BLEGATTDeviceName()+' does Not Responds to ('+serviceIterator+') :: '+ev_err_services_enumeration));
            }
           // console.log(' Return from '+serviceIterator);
         
              return null;
        }
        
       /* *********************************************** */
       /* *********************************************** */
       /* *********************************************** */
       /* *********************************************** */
       var serviceResult = null;
       
        async function DiscoverCompatServices(BLEServerPTR)
        {
            
			
              var aDiscoverCursorValue = (100/(BLEGATTServicesValid.length));
		BLEGATT_resolvedServicesCount = 0;	
		BLEGATT_resolvedServicesArray = [];
              serviceResult = null;
              
              // ****************************************************************
              // :: Standard API Equiv ::
              // :: https://developer.mozilla.org/en-US/docs/Web/API/BluetoothRemoteGATTService/getIncludedService
              // ****************************************************************
              
              BLEGATTAPIVersionImplementationWarning( 'GATT Discover Included Device Services', '(BluetoothGATTService).getIncludedService', ([]).getIncludedService+'::'+ ([]).getIncludedServices, true);
              
              
              for( var BLEServicecheck in BLEGATTServicesValid)
              {
                     try{
                            
                            console.log('***************************************************************************');
                            
                            // :: console.log(' BLEGATTDeviceSelected : '+BLEGATTDeviceSelected);
                            BLEGATTSetUIProgress('Recherche sur ('+BLEGATTDeviceName()+') ...<br />Check Compatible Bluetooth Services : ('+BLEServicecheck+'/'+BLEGATTServicesValid.length+') ['+BLEGATTServicesValid[ BLEServicecheck ]+'] <br />Valid ('+BLEGATT_resolvedServicesCount+')',(aDiscoverCursorValue*BLEServicecheck));
                            var serviceFound = false;
                           
                            BLEServerPTR = ( BLEServerPTR ||  BLEGATTServerHandle);
                            serviceResult = await asyncResolvingService(BLEServerPTR, BLEGATTServicesValid[ BLEServicecheck ])
                            .then(aServiceGatt=>{
                                // console.log('Discover Received Services '+aServiceGatt);
                                
                                if(!aServiceGatt){
                                   //  throw new Error('Invalid Service Response ...'); 
                                } /*else if(!aServiceGatt.getCharacteristic){
                                    console.log('Invalid Service Response:: ...('+BLEGATT_resolvedServicesCount+'): '+  BLEGATTServicesValid[ BLEServicecheck ]); 
                                }*/else
                                {
                                    // BLEGATTSetUIProgress('Validation Service Bluetooth sur ('+BLEGATTDeviceName()+') ...<br /> Service Bluetooth :'+BLEGATTServicesValid[ BLEServicecheck ],(aDiscoverCursorValue*BLEServicecheck));
                    
                                    console.log('**** Discovered :: serviceIterator ('+BLEGATT_resolvedServicesCount+'): '+ aServiceGatt);
                                    BLEGATT_resolvedServicesCount ++;
                                    BLEGATT_resolvedServicesArray.push(aServiceGatt);
                                    serviceFound = true;
                                }
                                console.log(' .... Got aServiceGatt('+BLEServicecheck+') :: ['+aServiceGatt+']');
                                return Promise.resolve([aServiceGatt]);
                            })/*.then(aCharcatericsReceived=>{
                                   
                                   //console.log('Received Characteritics .... ['+aCharcatericsReceived+'::'+aCharcatericsReceived.getCharacteristics+']');
                                    
                                 // BLEDevicesCharacteristics  
                                   return BLEGATTGetSpec(Promise.resolve([aCharcatericsReceived]));
                            })*/;
                            
                            
                            clearTimeout(BLEGATTDiscoverTimeOutHandle);
                            
                            if(serviceFound){
                            //console.log('Take a Nap ...');
                                   await takeanap_sleep(500);
                                   
                                   
                                                
                               console.log('###### serviceResult ... '+serviceResult+'::'+Promise.all([serviceResult.getCharacteristic])+'::'+Promise.all([serviceResult.getCharacteristics]));              
                          
                            //console.log('Clear to Nap ...');
                            }
                            //console.log('Take a Nap ...');
                            await takeanap_sleep(500);
                            //console.log('Clear to Nap ...');
                            
                            //        return serviceResult;
                     }catch(ev_err_each_service_disvcovery){
                         console.log('Error ev_err_each_service_disvcovery : '+ev_err_each_service_disvcovery);
                     }
              }
              console.log('Discover Services complete : '+BLEGATT_resolvedServicesArray.slice(0, ((BLEGATT_resolvedServicesArray.length  > 0)?BLEGATT_resolvedServicesArray.length:0)) );
              if(BLEGATT_resolvedServicesCount <= 2) 
              {
                     alert('Warning : Not Enough Valid Service \r\n Maybe BLEGATTServer is Crashed \r\n OR \r\n Device is OFF or is Not in Bluetooth Communication Mode \r\n OR \r\n This Device does not Validate Medical GATT Service .... \r\n  \r\n Please Check your Device, Retry or Reboot Your Computer !!');
              }else if(BLEGATT_resolvedServicesCount == 0)
              {
                     alert('Warning : No Valid Service \r\n Maybe BLEGATTServer is Crashed .... \r\n Retry or Please Reboot Your Computer !!');
              }
             
             
        }
        async function DiscoverCompatServicesWrapper(){
            var result = await DiscoverCompatServices();
			  BLEGATTSetUIProgress('Complete ... ('+BLEGATT_resolvedServicesCount+' Services discovered for ['+BLEGATTDeviceName()+'])',100);
            return result;
         }
                    
         
        
        
       /* *********************************************** */
       /* *********************************************** */
       /* *********************************************** */
       /* *********************************************** */
       
       function BLEGATTChrome(){
            alert('Unsupported Platform ... ');
       }
       /* *********************************************** */
       /* *********************************************** */
       /* *********************************************** */
       /* *********************************************** */
       
       function onServerGattDisconnected(){
            console.log('Device is disconnected ... ');
            if(BLEGATTDeviceSelected && ((BLEGATTServerHandleRetryReconnect ++) <= BLEGATTServerHandleRetryReconnectMax) ){
                console.log('trying to reconnect to Device ... ('+BLEGATTDeviceName()+' ['+BLEGATTDeviceSelected.UUID+'])');
                BLEGATTServerHandle = BLEGATTDeviceSelected.gatt.connect();
                if(BLEGATTServerHandle){
                    BLEGATTServerHandle = BLEGATTDeviceSelected.gatt;
                    console.log('Return server Handle ');
                    BLEGATTServerHandleRetryReconnect = 0;
                   return BLEGATTServerHandle; 
                }else{
                    BLEGATTServerHandle = BLEGATTDeviceSelected.gatt;
                    alert('Perte de connection avec le device ('+BLEGATTDeviceName()+' ['+BLEGATTDeviceSelected.UUID+'])');
                }
            }else{
                BLEGATTServerHandleRetryReconnect = 0;
              console.log('You Must Select a new device ... ');  
            }
            // **********************************
            // **********************************
            return Promise.resolve();
       }
       // **********************************
       // **********************************
       // **********************************
       // **********************************
       function BLEGATTNavigatorPromise(){
        
       }
       // **********************************
       // **********************************
       // **********************************
       // **********************************
       function BLEGATTDeviceName(){
			return ( ((BLEGATTDeviceSelected && BLEGATTDeviceSelected.name)?BLEGATTDeviceSelected:null) || {name:'Unknow Device Name'} ).name;
	   }
       // **********************************
       // **********************************
       // **********************************
       // **********************************
       function BLEGATTCheckSupportAndConnectDevice(aDeviceName){
            BLEGATTServerHandle =  null;
            BLEGATTDevice_userHadSelectedDevice = false;
            BLEGATTDeviceSelected = null;
            BLEGATTServerHandleRetryReconnect = 0;
			
			
			if(!BLEGATTCheckNoRadioSupport()){
				console.log(new Error('BLEGATTCheckSupportAndConnectDevice::NoRadioDeviceSupport was found ...'));
				return false;
			}
			
			
			
            try{
                
                
                console.log('Promise::Testing Bluetooth Device Radio ...');
                
                if(!BLEGATTCheckNoRadioSupport())
				{
					console.log('Error :: BLEGATTCheckSupportAndConnectDevice:: NoRadioFound ... ');
					return false;
				}
               
                console.log('Promise::Requesting Bluetooth Device...');
                
                
                let options = {
                               //
                               "acceptAllDevices":true,
                               // filters: [
                                  // {services: ['heart_rate']},
                                  // {services: [0x1802, 0x1803]},
                                  // {services: ['c48e6067-5295-48d3-8d5c-0395f61792b1']},
                                  // {name: 'ExampleName'},
                                 // {namePrefix: 'CheckMe'}
                                //],
                                //
                                // filters:[ {services: BLEGATTServicesValid} ],
                                optionalServices: BLEGATTServicesValid
                              };
                BLEGATTSetUIProgress('Recherche bluetooth ...',30);              
                var BLEGatDevice = navigator.bluetooth.requestDevice(options)
                .then( bluetoothDevice => {
                        
                    if(!bluetoothDevice){ throw new Error('No Device Found :'); }
                    
                    BLEGATTDeviceSelected = bluetoothDevice;
                    console.log('Promise::Try to Connect to Device ... '+BLEGATTDeviceName()+'::'+BLEGATTDeviceSelected.gatt);
                    
                    
                    BLEGATTDeviceSelected.addEventListener('gattserverdisconnected', onServerGattDisconnected);
                     
                    BLEGATTDevice_userHadSelectedDevice = true;
                    // BLEGATTServerHandle = BLEGATTDeviceSelected.gatt.connect();
                    BLEGATTSetUIProgress('Connection bluetooth sur ('+BLEGATTDeviceName()+') ...',50);  
                    return BLEGATTDeviceSelected.gatt.connect();

                }) .catch(ev_err_discovery_services=>{
                    
                    console.log( Error(' '+ev_err_discovery_services));
                    
                   if(BLEGATTDeviceSelected){
                        throw new Error(' Error ... \r\nMayBe this Device require Authentification and is not associated with your computer ...\r\nTech Reason : '+ev_err_discovery_services);
                   }else{
                        throw new Error(ev_err_discovery_services);
                   }
                }).then(async (serverGatt) => {
                    // Getting Services...
                    // BLEGATTServerHandle = serverGatt;
                    BLEGATTServerHandle = BLEGATTDeviceSelected.gatt;
                    
                    try{
                        console.log('Promise::Try to Discovering Services ...');
                        if(! serverGatt.getPrimaryService ){
                            alert(''+BLEGATTAPIVersionImplementationWarning( ' (This BlootoothLE API is OutDated ...) ', '(BluetoothRemoteGATTService).getPrimaryService', serverGatt.getPrimaryService, true ));
                            return null;
                        }     
                    }catch(ev_err_implementation_oudated){
                        console.log('server :: '+serverGatt);
                            throw new Error('Exception::This BlootoothLE API seems OutDated ... '+ev_err_implementation_oudated);
                    }
                    
                    
                    /*
                        serverGatt.getPrimaryService('heart_rate').then(serviceGatt => {
                          return Promise.all([
                              this._cacheCharacteristic(serviceGatt, 'body_sensor_location'),
                              this._cacheCharacteristic(serviceGatt, 'heart_rate_measurement'),
                          ]);
                        })
                        */
                    
                    
                     // console.log('Resolved services :: '+ resolvedServices.shift());
                     if(!BLEGATTServerHandle.connected){
                        console.log('not Connected for Resolving services :: ');
                        BLEGATTServerHandle = BLEGATTDeviceSelected.gatt.connect();
                        console.log('Shall be Connected to Resolve services :: '+BLEGATTServicesValid);
                        // return BLEGATTServerHandle;
                     }
                    
                     
                    // ********************************************************
                    // http://bluebirdjs.com/docs/api/promise.each.html
                    // ********************************************************
                     await takeanap_sleep(500);
                     BLEGATTSetUIProgress('Connectee sur ('+BLEGATTDeviceName()+') ...',100);
                      await takeanap_sleep(500);
                     console.log('Send Dicovering .... '+BLEGATTServerHandle+'::'+serverGatt);
                     var resultDiscoveredServices =  await DiscoverCompatServicesWrapper((serverGatt || 'nulll'));
                     console.log('return discovered ... ');
                     return resultDiscoveredServices;
                    
                }) .then(services=>{
                    
                    
                    console.log('Received Services '+services);
                    
                    })
                 
                 .catch(ev_err_discovery_services=>{
                    
                     if( !BLEGATTDevice_userHadSelectedDevice ){
                        
                        BLEGATTSetUIProgress('Recherche bluetooth annulee ...',100);
                    
                        // alert('Vous avez annulee la recherche Bluetooth ... ');
                     }else{
                        
                        console.log( Error('No Service found on Device :('+BLEGATTDeviceName()+') \r\n Reason : '+ev_err_discovery_services));
                        
                        BLEGATTSetUIProgress('Erreur bluetooth sur ('+BLEGATTDeviceName()+') ...<br /> Reason : '+ev_err_discovery_services,100);
                    
                        alert('('+BLEGATTDeviceName()+') Does not validate requirement ... \r\n'+ev_err_discovery_services);
                     }
                     return null;
                });
                
            }catch (ev_runscript_error_blerequest){
                alert('Error while running BLE request : '+ev_runscript_error_blerequest);
            }
      }
    /* *********************************************** */
    /* *********************************************** */
    /* *********************************************** */
    /* *********************************************** */
    
       async function BLEGATTGetSpec(aGAttserviceDescriptor, serviceCharacteristicsName){
                   
       }
                
        /* *********************************************** */
        /* *********************************************** */
        /* *********************************************** */
        /* *********************************************** */
        
		function BLEGATTCheckNoSupport(eventOrigin){
			console.log('BLEGATTCheckNoSupport ....');
			
			var sentenceStart = '<br />Please Download a Compatible Version of '+(navigator.appNameReal || 'Netscape');
			var sentenceEnd = '<br />Because : <br /> This one does not include full features for <br /> Bluetooth 4.0 (LE/GATT) Connectivity ...';
			
			var linkA = '<br /> - <a href="https://github.com/WebBluetoothCG/web-bluetooth/blob/master/implementation-status.md">https://github.com/WebBluetoothCG/web-bluetooth/blob/master/implementation-status.md</a>';
			var linkB = '<br /> - <a href="https://webbluetoothcg.github.io/web-bluetooth/#dom-bluetoothgattremoteserver-getprimaryservice" >https://webbluetoothcg.github.io/web-bluetooth/#dom-bluetoothgattremoteserver-getprimaryservice</a>';
			
			(aDocNodeElement_BLEGATTCheckSupportMessage).innerHTML = '**** This Functionnality still in Draft (October/2018) : **** <br /> '+linkA+' '+linkB+' <br />'+sentenceStart+' version Up To '+navigator.appVersionRealMinCompat+'<br /><br />You are using Version : '+navigator.appVersionReal +' '+ ((!navigator.userAgentIsChrome)?'<br />Compatible Version shall be : '+navigator.appVersionRealMinCompat+'<br />Or Download GoogleChrome version Up To '+ChromeVersionCompatMin+'<br /> Or Latest Beta Version ...':' ');    
			(aDocNodeElement_BLEGATTCheckSupportMessage).style.background ='#ED6161';
			
			 
			var funcUserAlert = function(){
				alert('Sorry your Version of ('+navigator.appNameReal+'['+navigator.appCodeName+':'+navigator.appVersionReal +']'+') '+sentenceEnd.replaceAll('<br />','\r\n'));
				return;
				};
			
			if(eventOrigin){
				setTimeout(funcUserAlert, 1000);
			}
		}
	  
        /* *********************************************** */
        /* *********************************************** */
        /* *********************************************** */
        /* *********************************************** */
        
		function BLEGATTCheckNoRadioSupport(eventOrigin){
			try{
				
				
				if(!navigator.bluetooth.getAvailability)
				{
					BLEGATTAPIVersionImplementationWarning( 'Radio Avaibility', 'navigator.bluetooth.getAvailability', navigator.bluetooth.getAvailability, true);
					return true;
				}
				
				
				if(navigator.bluetooth.getAvailability()){
					(aDocNodeElement_BLEGATTCheckSupportMessage).innerHTML = 'Good :: BLEGattCheckNoRadioSupport ... (Seems to have BLE Hardware)';
					return true;				
				}else{
					(aDocNodeElement_BLEGATTCheckSupportMessage).innerHTML = 'Error :: BLEGattCheckNoRadioSupport ... (Check your Bluetooth Hardware)';    
					return false;
				}
			}catch(ev_err_BLEGATTImplementation){
				console.log('Error :: BLEGattCheckNoRadioSupport ... ('+ev_err_BLEGATTImplementation+')');
				(aDocNodeElement_BLEGATTCheckSupportMessage).innerHTML = 'Error :: BLEGattCheckNoRadioSupport ... ('+ev_err_BLEGATTImplementation+')';    
			}
			
			return false;
		}
		
        /* *********************************************** */
        /* *********************************************** */
        /* *********************************************** */
        /* *********************************************** */
        
		function BLEGATTCheckDocumentElement_Init(){
			try{
				
				
				aDocNodeElement_bluetooth_progressbar		=  (document.getElementById('bluetooth_progressbar')||[]);
				aDocNodeElement_bluetooth_message			=  (document.getElementById('bluetooth_message')||[]);
				aDocNodeElement_bluetooth_btn_findDevices	       =  (document.getElementById('bluetooth_btn_findDevices')||[]);
				
				aDocNodeElement_BLEGATTCheckSupportMessage 	=  (document.getElementById('bluetooth_message')||document.getElementById('BLEGATTCheckSupportMessage')||[]) ;
				// alert('UIelement : '+aDocNodeElement_BLEGATTCheckSupportMessage);
				if(aDocNodeElement_BLEGATTCheckSupportMessage.value)
				{
					aDocNodeElement_BLEGATTCheckSupportMessage.value = 'No Web Bluetooth Support or Internal Error Occurred';
				}
				
				
				aDocNodeElement_bluetooth_btn_findDevices.onclick = function(){ };
				
				try{
				   
				/* *********************************************** */
					try{
						var isChromeStyleImplementation = (Chrome);
						if(isChromeStyleImplementation){
							
							if( (Chrome || [] ).bluetooth && (ChromeVersion >= 70) ){
							
								console.log('Chrome.bluetooth::'+(chrome || []).bluetooth);
								BLEGattConnectDevice = BLEGATTChrome;
								isChromeStyleImplementation = true;
							}else{
								throw new Error('Chrome::Incompatible Version ... ('+ChromeVersion+')');
							}
							
						}
					}catch(ev_err_doesitbeChrome){
						console.log('Chrome::Error::'+ev_err_doesitbeChrome);
						isChromeStyleImplementation = false;
					}
					
				/* *********************************************** */
					if( (!isChromeStyleImplementation) && navigator.bluetooth   ){
						try{
							// standardized Implementation from :: October 2018 :: https://webbluetoothcg.github.io/web-bluetooth/#availability-fingerprint
							// ::  && navigator.bluetooth.getAvailability
							if(navigator.bluetooth.requestDevice ){
								console.log('Navigator.bluetooth:: BLESupport[navigator.bluetooth]='+navigator.bluetooth+'::('+navigator.appNameReal+':'+navigator.appVersionReal+')');
								console.log('Navigator UserAgent:: '+navigator.userAgent);
								BLEGattConnectDevice = BLEGATTCheckSupportAndConnectDevice;
								
							}else{
								throw new Error('Error::Init::BLEGATT implementation Error ... (navigator.bluetooth.requestDevice=['+navigator.bluetooth.requestDevice+'])::(navigator.bluetooth.getAvailability=['+navigator.bluetooth.getAvailability+'])');
							}
							 
							if(!BLEGATTCheckNoRadioSupport()){
								console.log('Error::Init::BLEGATTCheckNoRadioSupport ...');
							}else
							
							if(isChromeStyleImplementation && (ChromeVersion <= 69) ){
								alert(' Update to Version up to 70 of GoogleChrome ');
								BLEGattConnectDevice = BLEGATTCheckNoSupport;
							} else if(navigator.userAgentIsGecko && (GeckoVersion <= 63) ) {
								// alert(' Update to Version up to 70 of Gecko(Nightly Firefox / Mozilla Firefox) ');
								BLEGattConnectDevice = BLEGATTCheckNoSupport;
							}
							
						}catch(ev_err_implementation_webble_promise){
							throw new Error('Promise::Error::'+ev_err_implementation_webble_promise); 
						}
					}else if(! navigator.bluetooth ){
						throw new Error(' No bluetooth Support was Found for ('+navigator.appNameReal+':'+(GeckoVersion)+'::BLESupport[navigator.bluetooth]='+navigator.bluetooth+')');
					}else if( isChromeStyleImplementation ) {
						console.log(' Chrome BLE Status ??? ');
						// :: BLEGATTCheckNoSupport(); 
					}
				   
					
					if((navigator.userSystem.indexOf('win') != (-1)) && (parseInt(navigator.userSystemVersion) < 10)){
						console.log(Error('That Version of '+navigator.userSystem+' ('+navigator.userSystemVersion+') may not support WebBluetooth BLE ...'));
					}else if(BLEGATTCheckNoRadioSupport()){
						 // finally show compat message ... 
						setTimeout(()=>{
							
							
							if(aDocNodeElement_bluetooth_btn_findDevices.innerHTML)
							{
								aDocNodeElement_bluetooth_btn_findDevices.innerHTML = 'Recherche Bluetooth';
								
							}
							aDocNodeElement_bluetooth_btn_findDevices.onclick = BLEGattConnectDevice;
							aDocNodeElement_bluetooth_btn_findDevices.style.visibility  = 'visible';
							aDocNodeElement_bluetooth_progressbar.style.visibility  = 'visible';
							aDocNodeElement_bluetooth_progressbar.parentNode.style.visibility  = 'visible';
							
							aDocNodeElement_bluetooth_btn_findDevices.style.display  = 'block';
							aDocNodeElement_bluetooth_progressbar.style.display  = 'block';
							aDocNodeElement_bluetooth_progressbar.parentNode.style.display  = 'block';
							
							
						   aDocNodeElement_BLEGATTCheckSupportMessage.innerHTML = ' Click Find to Connect to a Nearest BLE IOTs...';    
						   }, 500) ;
					
					}
				/* *********************************************** */
				}catch(ev_err_ble_implementation){
					console.log('INIT::CheckCompat::Exception occurence : '+ev_err_ble_implementation);
					setTimeout(function(){BLEGATTCheckNoSupport(0)}, 500) ;
					// BLEGATTCheckNoSupport(); 
				}
			/* *********************************************** */
			}catch(ev_err_ble_implementation_init){
				console.log(Error('INIT::BootstrapCompat&UIElements::Exception occurence : '+ev_err_ble_implementation_init));
				setTimeout(function(){BLEGATTCheckNoSupport(0)}, 500) ;
				// BLEGATTCheckNoSupport(); 
			}	
		}
        /* *********************************************** */
        /* *********************************************** */
        /* *********************************************** */
        /* *********************************************** */
        setTimeout(function(){BLEGATTCheckDocumentElement_Init(0)}, 500) ; 
    
