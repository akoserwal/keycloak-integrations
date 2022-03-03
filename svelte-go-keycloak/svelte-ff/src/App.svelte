<script lang="ts">
  import logo from "./assets/svelte.png";
  import golang_logo from "./assets/go.png";
  import keycloak_logo from "./assets/keycloak.png";
  import Keycloak, { KeycloakInitOptions } from "keycloak-js";

  // Keycloak
  let instance = {
    url: "http://127.0.0.1:8081",
    realm: "keycloak-demo",
    clientId: "svelte-app",
  };

  let keycloak = Keycloak(instance);
  let initOptions: KeycloakInitOptions = { onLoad: "login-required" };
  keycloak
    .init(initOptions)
    .then(function (authenticated) {
      console.info("Authenticated");
    })
    .catch(function () {
      alert("failed to initialize");
    });

  //Count API
  let apiURL = "http://127.0.0.1:8086/count";
  let count: number = 0;
  async function getCount() {
    const response = await fetch(apiURL, {
      method: "GET",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${keycloak.token}`,
      },
    });
    if (!response.ok) {
      console.log(response);
    }
    let count = await response.json();
    return count;
  }

  const increment = async () => {
    //update token
    keycloak?.updateToken(50).then(async function () {
      let countResp = await getCount();
      count = countResp.count;
    });
  };
</script>

<main>
  <h1>Secured Svelte-app <img src={logo} alt="Svelte Logo" />count frontend</h1>
  <h1>Secured Golang<img src={golang_logo} alt="golang" /> count service</h1>
  <h1>using Keycloak <img src={keycloak_logo} alt="keycloak" /></h1>

  <button on:click={increment}>
    Clicks: {count}
  </button>
</main>

<style>
  :root {
    font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Oxygen,
      Ubuntu, Cantarell, "Open Sans", "Helvetica Neue", sans-serif;
  }

  main {
    text-align: center;
    padding: 1em;
    margin: 0 auto;
  }

  img {
    height: 3rem;
    width: 3rem;
  }

  h1 {
    color: #ff3e00;
    text-transform: uppercase;
    font-size: 1.8rem;
    font-weight: 80;
    line-height: 1.1;
    margin: 2rem auto;
    max-width: 14rem;
  }

  p {
    max-width: 14rem;
    margin: 1rem auto;
    line-height: 1.35;
  }

  @media (min-width: 480px) {
    h1 {
      max-width: none;
    }

    p {
      max-width: none;
    }
  }
  button {
    font-family: inherit;
    font-size: inherit;
    padding: 1em 2em;
    color: #ff3e00;
    background-color: rgba(255, 62, 0, 0.1);
    border-radius: 2em;
    border: 2px solid rgba(255, 62, 0, 0);
    outline: none;
    width: 200px;
    font-variant-numeric: tabular-nums;
    cursor: pointer;
  }

  button:focus {
    border: 2px solid #ff3e00;
  }

  button:active {
    background-color: rgba(255, 62, 0, 0.2);
  }
</style>
