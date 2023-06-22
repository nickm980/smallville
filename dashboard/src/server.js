const isDevMode = true;
const url = isDevMode ? "http://localhost:8080" : "";

async function getConversations() {
  return fetch(url + "/conversations").then((response) => response.json());
}

async function updateGameState() {
  return fetch(url + "/state", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify({}),
  });
}

async function getAgents() {
  return fetch(url + "/agents") // Replace 'your-name' with the actual name parameter
    .then((response) => response.json());
}

async function getLocations() {
  return fetch(url + "/locations") // Replace 'your-name' with the actual name parameter
    .then((response) => response.json());
}

async function getGameInfo() {
  return fetch(url + "/info", {
    method: "GET",
    headers: {
      "Content-Type": "application/json",
    },
  }).then((response) => response.json());
}

async function changeLocation(name, state) {
  return fetch(url + "/locations/" + name, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify({
      state: state,
    }),
  }).then((response) => response.json());
}

async function ask(name, question) {
  return fetch(url + "/agents/" + name + "/ask", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify({
      question: question,
    }),
  }).then((response) => response.json());
}

export {
  getConversations,
  updateGameState,
  getGameInfo,
  getLocations,
  getAgents,
  changeLocation,
  ask,
};
