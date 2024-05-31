export interface Pagebase {
  constructor: any;
  ngOnInit: any;
  ngOnDestroy: any;
  ngOnChanges: any;
  ngDoCheck: any;
  ngAfterContentInit: any;
  ngAfterContentChecked: any;
  ngAfterViewInit: any;
  ngAfterViewChecked: any;
  title?: string;
  standalone?: boolean;
  imports?: any[];
  templateUrl?: string;
  styleUrl?: string;
  selector?: string;
  providers?: any[];
  exports?: any[];
  declarations?: any[];
  entryComponents?: any[];
  bootstrap?: any[];
  schemas?: any[];
  animations?: any[];
  id?: string;
  name?: string;
}
