import React from 'react';
import { HashRouter as Router, Route, Switch } from 'react-router-dom';
import GlobalStyle from './components/GlobalStyles';
import Grid from './components/Grid/Grid';
import Lobby from './containers/Lobby';
import Play from './containers/Play';
import Welcome from './containers/Welcome';
import NotificationProvider from './contexts/notifications';
import PlayerNameProvider from './contexts/playerDetails';
import SocketProvider from './contexts/socket';

export default function App() {
  return (
    <Router>
      <PlayerNameProvider>
        <NotificationProvider>
          <SocketProvider>
            <GlobalStyle />
            <Grid>
              <Switch>
                <Route path="/game/:difficulty/:gameId">
                  <Play />
                </Route>
                <Route exact path="/games">
                  <Lobby />
                </Route>
                <Route path="/">
                  <Welcome />
                </Route>
              </Switch>
            </Grid>
          </SocketProvider>
        </NotificationProvider>
      </PlayerNameProvider>
    </Router>
  );
}
