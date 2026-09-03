/* ******************* ****************** */
/* ******************* UNIT_MAIN ****************** */
/* ******************* ****************** */

import {
  SCREEN_OBJECTS,
  StarRefreshLoop,
  drawStatic,
  initMap,
  runDemoMockupObject,
} from "./UNIT_GRAPHICS_LOOP.js";

import {
  loadSoundLib,
  StartSoundMainTheme,
  getSoundLib,
} from "./UNIT_SOUND_LIB.js";

const mainUnit = async () => {
  console.info(" ********** mainUnit");
  Promise.resolve(initMap())
    .then(() => {
      console.log("Map initialized:", SCREEN_OBJECTS);
      // Start the refresh loop at 60 FPS
      Promise.resolve(StarRefreshLoop(drawStatic, 60))
        .then(() => {
          console.info(" ******* Refresh loop started");
        })
        .then(() => {
          console.info("Refresh loop started");
        })
        .catch((error) => {
          console.error("Error starting refresh loop:", error);
        });
    })
    .catch((error) => {
      console.error("Error initializing map:", error);
    });

  // Load the sound library and start the sound
  await Promise.resolve(loadSoundLib())
    .then(() => {
      console.info("Sound library loaded:", getSoundLib());
      Promise.resolve(StartSoundMainTheme())
        .then(() => {
          console.info("Sound main theme started");
        })
        .catch((error) => {
          console.error("Error starting sound main theme:", error);
        });
    })
    .catch((error) => {
      console.error("Error loading sound library:", error);
    });
  /* ******************* ****************** */
  // Run the demo mockup objects
  runDemoMockupObject();
  /* ******************* ****************** */
};

/* ******************* ****************** */
// run the main unit when the window is ready
/* ******************* ****************** */
addEventListener("DOMContentLoaded", (event) => {
  console.info(" ********** DOMContentLoaded");
  mainUnit();
});

export { mainUnit };
