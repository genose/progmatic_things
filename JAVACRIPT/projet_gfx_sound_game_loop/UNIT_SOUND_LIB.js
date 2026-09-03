/* ******************* ****************** */
/* ******************* UNIT_SOUND_LIB ****************** */
/* ******************* ****************** */

let soundLib = {}; // load at runtime

let soundChannels = {}; // object to hold sound channels, e.g., { main: HTMLAudioElement, aux1: HTMLAudioElement, ... }
let soundMaxChannels = 5; // maximum number of sound channels
const soundChannelNames = {
  MAIN: MAIN,
  AUX1: "aux1",
  AUX2: "aux2",
  AUX3: "aux3",
  AUX4: "aux4",
};
const soundChannelStatesEnum = {
  STOPPED: STOPPED,
  PLAYING: "playing",
  PAUSED: "paused",
};

let soundChannelStates = {}; // object to hold the state of each channel, e.g., { main: STOPPED, aux1: "playing", ... }

/* ************ ****************** */
// Initialize sound channels
const soundMainChannel = buildSoundChannel(MAIN);
const soundAuxiliaryChannels = soundChannelNames
  .filter((name) => name !== MAIN)
  .map((name) => buildSoundChannel(name));

initSoundChannels();

/* ************ ****************** */
function buildSoundChannel(channelName) {
  if (!soundChannels[channelName]) {
    const audioElement = document.createElement("audio");
    audioElement.id = channelName;
    soundChannels[channelName] = audioElement;
    document.body.appendChild(audioElement);
  }
  return soundChannels[channelName];
}
/* ************ ****************** */
function initSoundChannels() {
  soundChannelNames.forEach((channelName) => {
    buildSoundChannel(channelName);
    soundChannelStates[channelName] = STOPPED; // Initialize state
  });
}
/* ************ ****************** */
function getSoundChannelState(channelName) {
  return soundChannelStates[channelName] || STOPPED;
}
/* ************ ****************** */
function getSoundLib() {
  return soundLib;
}
/* ************ ****************** */
function getSoundMaxChannels() {
  return soundMaxChannels;
}
/* ************ ****************** */
function getSoundChannels() {
  return soundChannels;
}
/* ************ ****************** */
function getSoundChannel(channelName) {
  return soundChannels[channelName] || null;
}
/* ************ ****************** */
function getSoundMainChannel() {
  return soundMainChannel;
}
/* ************ ****************** */
function getSoundAuxiliaryChannels() {
  return soundAuxiliaryChannels;
}
/* ************ ****************** */
function setSoundAuxiliaryState(channelIndex, state) {
  if (channelIndex >= 0 && channelIndex < soundAuxiliaryChannels.length) {
    const channel = soundAuxiliaryChannels[channelIndex];
    if (state === PLAY) {
      channel.play().catch((error) => {
        console.error("Error playing auxiliary sound:", error);
      });
    } else if (state === PAUSED) {
      channel.pause();
    } else if (state === STOPPED) {
      channel.pause();
      channel.currentTime = 0;
    }
  } else {
    console.error("Invalid auxiliary channel index:", channelIndex);
  }
}

function StartSoundMainTheme() {
  // code pour initialiser le son
  console.log(" ********** StartSound");
  if (!soundLib.mainSound || soundLib.mainSound.length === 0) {
    console.error("No main sound available");
    return;
  }

  let soundMainChannel = getSoundMainChannel(); // Ensure the main channel is built
  if (!soundMainChannel) {
    soundMainChannel = buildSoundChannel("soundMainTheme");
  }

  soundMainChannel.id = "soundMainTheme";
  soundMainChannel.src = soundLib.mainSound[0].src;
  soundMainChannel.loop = soundLib.mainSound[0].loop || false;
  soundMainChannel.play().catch((error) => {
    console.error("Erreur lors de la lecture du son :", error);
  });
}

function changeSoundMainTheme(soundName) {
  // code pour changer le son
  console.log(" ********** chanegeSoundMainTheme");
  const sound = (soundLib.mainSound || []).find((s) => s.name === soundName);
  if (sound) {
    if (!soundMainChannel.paused) {
      soundMainChannel.pause();
      soundMainChannel.currentTime = 0;
    }
    soundMainChannel.src = sound.src;
    soundMainChannel.loop = sound.loop || false;
    soundMainChannel.play().catch((error) => {
      console.error("Erreur lors de la lecture du son :", error);
    });
  } else {
    console.error("Sound not found:", soundName);
  }
}

function StopSoundMainTheme() {
  // code pour stopper le son
  console.log(" ********** StopSound");
  soundMainChannel.pause();
  soundMainChannel.currentTime = 0;
}

// Load the sound library with sound objects
function loadSoundLib() {
  return window
    .fetch("soundLib.json")
    .then((response) => response.json())
    .then((data) => {
      soundLib = data;
      console.log("Sound library loaded:", soundLib);
      return soundLib;
    })
    .catch((error) => {
      console.error("Error loading sound library:", error);
      throw error;
    });
}
