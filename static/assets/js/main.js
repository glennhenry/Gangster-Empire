const flashVersion = "11.3.0";

$(document).ready(function () {
  startGame();
});

function startGame() {
  $("#loading").show();

  const flashVars = {
    path: "/game/",
    gameId: "4",
    connectionId: "private",
    clientAPI: "javascript",
    useSSL: 0,
  };

  const params = {
    allowScriptAccess: "always",
    allowFullScreen: "true",
    allowNetworking: "all",
    wmode: "direct",
    bgColor: "#000000",
  };

  const attributes = { id: "game", name: "game" };

  swfobject.embedSWF(
    "/game/GoodgameGangster.swf",
    "game-container",
    "100%",
    "100%",
    flashVersion,
    "swf/expressinstall.swf",
    flashVars,
    params,
    attributes,
    function (e) {
      if (!e.success) {
        showNoFlash();
      } else {
        console.log("CacheBreaker loaded → will pull in Game SWF");
      }
    }
  );
}

function showNoFlash() {
  $("#noflash").show();
}
