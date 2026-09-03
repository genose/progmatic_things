/* ******************* ****************** */
/* ******************* UNIT_GRAPHICS_LOOP ****************** */
/* ******************* ****************** */

let SCREEN_UPDATE_TABLE = []; // Empty array to hold objects for screen updates
let SCREEN_UPDATE_TABLE_MAX = 100; // Maximum number of objects in the update table
let SCREEN_UPDATE_TABLE_MIN = 0; // Minimum number of objects in the update table

let SCREEN_OBJECTS = []; // Empty array to hold objects for screen updates

// Game canvas storage for objects
let SCREEN_CANVAS_OBJECT = [];
// Game canvas drawing context
let SCREEN_CANVAS = document.getElementById("screenCanvas");

/* ******************* ****************** */
// Loading the SCREEN_OBJECTS with graphics and data
/* ******************* ****************** */
function loadSCREEN_OBJECTS() {
    window.fetch("objectMap.json")
        .then(response => response.json())
        .then(data => {
            SCREEN_OBJECTS = data;
            console.log("SCREEN_OBJECTS loaded:", SCREEN_OBJECTS);
        })
        .catch(error => {
            console.error("Error loading SCREEN_OBJECTS:", error);
        });
    /* *************
        MODELE DEFINITION DES OBJETS
******** */
        return [{
                name: "explosion",
                graphics: {
                frameRange: 15,
                frameCount: 15,
                {
                name: "bonhomme",
                graphics: {
                    frameBase: 15,
                    frameCount: 15,
                    frameRemoveWhenAtZeroFrameCount: false,
                    animationType: "walk",
                    sprite:{
                    spritesheet: "bonhomme.png",
                    spriteWidth: 64,
                    spriteHeight: 64,
                    spritesFramesSrc: [
                        { 
                            animationType: "walk",
                            frameRange: () =>{return new range(0, 10)} ,
                            // case ou le sprite est variable
                            srcVaring:{
                                srcOrientation:"left",
                                srcPath: "bonhomme/walk/sprite_[xxx]_orientation_[xxx].png",
                            },
                            // case ou le sprite est fixe
                            src: {
                                srcOrientation:"left",
                                srcPath: "bonhomme/walk/sprite_[xxx]_orientation_[xxx].png",
                            },
                            repeat: true,
                            removeWhenAtZeroFrameCount: true
                        }
                    ]
                    }
                },
                data: { x: 20, y: 120 }, // ECS_OBJECT
        }


/* *************
        return [
            {
                name: "bonhomme",
                graphics: {
                    frameBase: 15,
                    frameCount: 15,
                    frameRemoveWhenAtZeroFrameCount: false,
                    animationType: "walk",
                    sprite:{
                    spritesheet: "bonhomme.png",
                    spriteWidth: 64,
                    spriteHeight: 64,
                    spritesFramesSrc: [
                        { 
                            animationType: "walk",
                            frameRange: () =>{return new range(0, 10)} ,
                            // case ou le sprite est variable
                            srcVaring:{
                                srcOrientation:"left",
                                srcPath: "bonhomme/walk/sprite_[xxx]_orientation_[xxx].png",
                            },
                            // case ou le sprite est fixe
                            src: {
                                srcOrientation:"left",
                                srcPath: "bonhomme/walk/sprite_[xxx]_orientation_[xxx].png",
                            },
                            repeat: true,
                            removeWhenAtZeroFrameCount: true
                        },
                        { 
                            animationType: "jump",
                            frameRange: () =>{return new range(0, 10)} ,
                            srcVaring: "bonhomme/jump/sprite_[xxx]_orientation_[xxx].png",
                            repeat: false,
                            removeWhenAtZeroFrameCount: true
                        },
                        {
                            animationType: "run",
                            frameRange: () =>{return new range(0, 10)} ,
                            srcVaringOrientation:"left",
                            // replace [xxx] par le numero de frame et [orientation] par left ou right
                            srcVaring: "bonhomme/run/sprite_[xxx]_orientation_[xxx].png",
                            repeat: true,
                            removeWhenAtZeroFrameCount: false
                        },
                        {
                            animationType: "attack",
                            frameRange: () =>{return new range(0, 10)} ,
                            srcVaringOrientation:"left",

                            srcVaring: "bonhomme/attack/sprite_[xxx]_orientation_[xxx].png",
                            repeat: false,
                            removeWhenAtZeroFrameCount: true
                        },
                        {
                            animationType: "die",
                            frameRange: () =>{return new range(0, 10)} ,
                            srcVaring: "bonhomme/die/sprite_[xxx].png",
                            repeat: false,
                            removeWhenAtZeroFrameCount: true
                        }
                    ]
                    }
                },
                data: { x: 20, y: 120 }, // extract  ECS_OBJECT
            },

            {
                name: "explosion",
                graphics: {
                frameRange: 15,
                frameCount: 15,
                frameRemoveWhenAtZeroFrameCount: true,
                },
                data: { x: 20, y: 120 }, // ECS_OBJECT
            },
            {
                name: "tank",
                graphics: {
                frameBase: 15,
                frameCount: 15,
                frameRemoveWhenAtZeroFrameCount: true,
                },
                data: { x: 20, y: 120 }, // ECS_OBJECT
            },
        ];
    ************* */
}

/* ******************* ****************** */
// Loading the object map with graphics and data
/* ******************* ****************** */
function loadObjectMap() {
    window.fetch("objectMap.json")
        .then(response => response.json())
        .then(data => {
            const objectMap = data;
            console.log("Object map loaded:", objectMap);
        })
        .catch(error => {
            console.error("Error loading object map:", error);
        });
}
/* ******************* ****************** */
//
/* ******************* ****************** */
function addObjectToMapCanvas(object) {
    // object = { name: "bonhomme", graphics: { frameBase: 15, frameCount: 15, frameRemoveWhenAtZeroFrameCount: true }, data: { x: 20, y: 120 }, callback: function() { console.log("callback triggered"); } }
    SCREEN_CANVAS_OBJECT.push(object);
    console.log("Object added to map canvas:", object);
    canvasDrawObject(object);
}

/* ******************* ****************** */
//
/* ******************* ****************** */
function removeObjectFromMapCanvas(object) {
    // object = { name: "bonhomme", graphics: { frameBase: 15, frameCount: 15, frameRemoveWhenAtZeroFrameCount: true }, data: { x: 20, y: 120 }, callback: function() { console.log("callback triggered"); } }
    const index = SCREEN_CANVAS_OBJECT.indexOf(object);
    if (index > -1) {
        SCREEN_CANVAS_OBJECT.splice(index, 1);
        console.log("Object removed from map canvas:", object);
        // Optionally clear the canvas or redraw remaining objects
        const ctx = SCREEN_CANVAS.getContext("2d");
        ctx.clearRect(0, 0, SCREEN_CANVAS.width, SCREEN_CANVAS.height);
        SCREEN_CANVAS_OBJECT.forEach((obj) => {
            canvasDrawObject(obj);
        });
    } else {
        console.error("Object not found in map canvas:", object);
    }
}
/* ******************* ****************** */
//
/* ******************* ****************** */
function canvasDrawObject(arg_object) {
    // object = { name: "bonhomme", graphics: { frameBase: 15, frameCount: 15, frameRemoveWhenAtZeroFrameCount: true }, data: { x: 20, y: 120 }, callback: function() { console.log("callback triggered"); } }
    const ctx = SCREEN_CANVAS.getContext("2d");
    // Assuming the object has a sprite image to draw
    const img = new Image();
    let objList = arg_object ? [arg_object] : SCREEN_CANVAS_OBJECT;
    for (const obj of objList) {
        img.src = obj.graphics.sprite.spritesheet; // Assuming the spritesheet is defined in the graphics
        img.onload = () => {
            ctx.drawImage(img, obj.data.x, obj.data.y);
            console.log("Object drawn on canvas:", obj);
        };
    }
}


/* ******************* ****************** */
// code a appeler pour alimenter eventLoop
/* ******************* ****************** */
function eventFromMap(click, objectFromMap) {
  // click = { x: 20, y: 120 }
  // calcul_colision = function() { return true;   }

    findObjectFromMap = objectFromMap.find((obj) => {
        // calcul de colision
        if (click.x >= obj.data.x && click.x <= obj.data.x + obj.graphics.width &&
            click.y >= obj.data.y && click.y <= obj.data.y + obj.graphics.height) {
            return true;
        }
        return false;
    });

    if (findObjectFromMap) {
        eventCallBack(findObjectFromMap);
    } else {
        console.log("No object found at the clicked position.");
    }
}
/* ******************* ****************** */
//
/* ******************* ****************** */
function eventCallBack(arg_obj) {

  // arg_obj = { name: "bonhomme", graphics: { frameBase: 15, frameCount: 15, frameRemoveWhenAtZeroFrameCount: true }, data: { x: 20, y: 120 }, callback: function() { console.log("callback triggered"); } }
  SCREEN_UPDATE_TABLE.push({
    name: arg_obj.name,
    graphics: arg_obj.graphics || {
        frameBase: 15,
        frameCount: 15,
        frameRemoveWhenAtZeroFrameCount: true,
    },
    data: { arg_obj },
    trigger:
      arg_obj.callback ||
      function () {
        console.error(" no callback trigger");
      },
  });
}
/* ******************* ****************** */
// 
/* ******************* ****************** */
var StarRefreshLoop = function (fn, fps) {
  // Use var then = Date.now(); if you
  // don't care about targetting < IE9
  var then = new Date().getTime();

  // custom fps, otherwise fallback to 60
  fps = fps || 60;
  var interval = 1000 / fps;

  return (function loop(time) {
    requestAnimationFrame(loop);

    // again, Date.now() if it's available
    var now = new Date().getTime();
    var delta = now - then;

    if (delta > interval) {
      // Update time
      // now - (delta % interval) is an improvement over just
      // using then = now, which can end up lowering overall fps
      then = now - (delta % interval);

      // call the fn
      if ((typeof fn === "function") && (SCREEN_UPDATE_TABLE.length > 0)){
            fn();
      }    
     
    }
  })(0);
};
/* ******************* ****************** */
//
/* ******************* ****************** */
function drawStatic() {
  console.log(" ******** refresh  : frame ");

  // traiter les objets SCREEN_UPDATE_TABLE
    SCREEN_UPDATE_TABLE.forEach((element, index) => {
    // trigger l'event de l'objet
    element.trigger(element.data);
    // decrementer le compteur de frame
    element.graphics.frameCount--;
    //
        if (element.graphics.frameCount <= 0 && element.graphics.frameRemoveWhenAtZeroFrameCount) {
            SCREEN_UPDATE_TABLE[index] = 0;
    } else {
      element.graphics.frameCount = element.graphics.frameBase;
    }
  });
  // netoyage de la table
  for (let i = SCREEN_UPDATE_TABLE.length - 1; i >= 0; i--) {
    if (SCREEN_UPDATE_TABLE[i] === 0) {
      SCREEN_UPDATE_TABLE.splice(i, 1);
      i = SCREEN_UPDATE_TABLE.length - 1;
    }
  }
}
/* ******************* ****************** */
//
/* ******************* ****************** */
function initMap() {
  console.log(" ********** initMap");
  loadObjectMap();
  loadSCREEN_OBJECTS();
}

/* ******************* ****************** */
// 
/* ******************* ****************** */
addEventListener("click", (event) => {
    const click = { x: event.clientX, y: event.clientY };
    console.log("Click event at:", click);
    eventFromMap(click, SCREEN_OBJECTS);
});


/* ******************* ****************** */
// DEMO OBJECTS
/* ******************* ****************** */
const MockupObject =[
        { "Bonhomme":   {   "name": "bonhomme 1", 
                            "graphics": { "frameBase": 15, "frameCount": 15, "frameRemoveWhenAtZeroFrameCount": true }, 
                            "data": { "x": 20, "y": 10 }, 
                            "callback":  () =>  { console.log("callback triggered") } 
                        } 
        },
        { "Bonhomme":   {   "name": "bonhomme 2", 
                            "graphics": { "frameBase": 15, "frameCount": 15, "frameRemoveWhenAtZeroFrameCount": true },
                            "data": { "x": 60, "y": 120 },
                            "callback":  () =>  { console.log("callback triggered") }
                        }
        },
        { "Explosion": {    "name": "explosion 1", 
                            "graphics": { "frameBase": 15, "frameCount": 15, "frameRemoveWhenAtZeroFrameCount": true }, 
                            "data": { "x": 220, "y": 120 }, 
                            "callback": () => { console.log("callback triggered") } 
                        } 
        }, 
        { "Tank":       {   "name": "tank 1", 
                            "graphics": { "frameBase": 15, "frameCount": 15, "frameRemoveWhenAtZeroFrameCount": true },
                            "data": { "x": 260, "y": 20 },
                            "callback": () => { console.log("callback triggered") } 
                        } 
        }
    ];

function runDemoMockupObject() {
    for (const obj of MockupObject) {
        if (Object.keys(obj).length > 0) {
            const key = Object.keys(obj)[0];
            if (SCREEN_OBJECTS.findIndex(o => o.name === obj[key].name) === -1) {
                SCREEN_CANVAS_OBJECT.push(obj[key]);
                console.log("Mockup object added to SCREEN_OBJECTS:", obj[key]);
            } else {
                console.log("Mockup object already exists in SCREEN_OBJECTS:", obj[key]);
            }
        }
    }    
    canvasDrawObject(null);        
}

export {
    SCREEN_UPDATE_TABLE,
    SCREEN_UPDATE_TABLE_MAX,
    SCREEN_UPDATE_TABLE_MIN,
    SCREEN_OBJECTS,
    SCREEN_CANVAS_OBJECT,
    SCREEN_CANVAS,
    loadSCREEN_OBJECTS,
    loadObjectMap,
    addObjectToMapCanvas,
    removeObjectFromMapCanvas,
    canvasDrawObject,
    eventFromMap,
    eventCallBack,
    StarRefreshLoop,
    drawStatic,
    initMap,
    MockupObject,
    runDemoMockupObject
};
