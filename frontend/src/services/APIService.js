import axios from 'axios';

export const listAvailableGames = () =>
  axios.get('/api/lobby')
    .then((response) => response.data)
    .catch((error) => console.error(error));

export const newGame = (difficulty, playerDto) =>
  axios.post(`/api/game/${difficulty}`, playerDto)
    .then((response) => response.data)
    .catch((error) => console.error(error));

export const startGame = (gameId, playerDto) =>
  axios.put(`/api/game/${gameId}/start`, playerDto)
    .then((response) => response.data)
    .catch((error) => console.error(error));

export const joinExistingGame = (gameId, playerDto) =>
  axios.put(`/api/game/${gameId}`, playerDto)
    .then((response) => response.data)
    .catch((error) => console.error(error));

export const listInitialQuestionDtos = (gameId, playerId) =>
  axios.get(`/api/game/${gameId}/${playerId}`)
    .then((response) => response.data)
    .catch((error) => console.error(error));
