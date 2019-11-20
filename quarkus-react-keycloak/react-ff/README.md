This project was bootstrapped with [Create React App](https://github.com/facebook/create-react-app).

## Available Scripts

In the project directory, you can run:

### `yarn start`

set keycloak client configuration as init options in index.js
```
let initOptions = {
    url: 'http://127.0.0.1:8081/auth', realm: 'keycloak-demo', clientId: 'react-ff', onLoad: 'login-required', promiseType: 'native'
}
```

Runs the app in the development mode.<br />
Open [http://localhost:3000](http://localhost:3000) to view it in the browser.







