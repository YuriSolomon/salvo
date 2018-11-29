getData();

function getData() {
    var app = new Vue({
        el: '#app',
        data: {
            listData: [],
            playersScore: [],
            top25score: [],
            gamesData: [],
            loginOrSignUp: true,
            errorMessage: [],
            errorStatus: false,
            res: [],
            userIslogged: false,
            leaderboardOrGames: true,
            loginOrBody: true
        },
        beforeCreate() {
            fetch('../api/leaderboard')
                .then(response => response.json())
                .then(json => {
                    this.listData = json;
                    this.getScore(this.listData);
                });
            fetch(`../api/games`)
                .then(response => response.json())
                .then(json => {
                    this.gamesData = json;
                    this.gamesData.games.sort((fst, snd) => snd.players.length - fst.players.length);
                    if (this.gamesData.current != null) {
                        this.userIslogged = true;
                    } else {
                        this.userIslogged = false;
                    }
                    console.log(this.gamesData)
                    this.getBlueBacground();
                });
        },
        methods: {
            getScore(playersList) {
                playersList.forEach(player => {
                    let playerInfo = {};
                    let totalScore = 0;
                    let wins = 0;
                    let loses = 0;
                    let ties = 0;
                    player.score.forEach(score => {
                        if (score.gameScore.playersScore == 1) {
                            wins++;
                        } else if (score.gameScore.playersScore == 0.5) {
                            ties++;
                        } else {
                            loses++;
                        }
                    })
                    totalScore = wins + (ties / 2);
                    playerInfo = {
                        "userName": player.userName,
                        "totalScore": totalScore,
                        "wins": wins,
                        "loses": loses,
                        "ties": ties
                    };
                    this.playersScore.push(playerInfo);
                    this.playersScore.sort((fst, snd) => snd.totalScore - fst.totalScore);
                })
                this.playersScore.forEach(player => {
                    if (this.top25score.length <= 25) {
                        this.top25score.push(player);
                    } else if (player.totalScore == this.top25score[this.top25score.length - 1].totalScore) {
                        this.top25score.push(player);
                    }
                })
            },
            login() {
                let email = document.getElementById("email").value.toLowerCase();
                let password = document.getElementById("password").value;

                $.post("/api/login", {
                        email: email,
                        password: password
                    })
                    .then(response => {
                        // console.log("logged in"),
                        console.log(JSON.stringify(response)),
                            location.reload();
                    })
                    .catch(error => console.error('Error:', error))
            },
            logout() {
                $.post("/api/logout").done(function () {
                    console.log("logged out");
                });
                location.reload();
            },
            loginChange() {
                if (this.loginOrSignUp == true) {
                    this.loginOrSignUp = false;
                } else {
                    this.loginOrSignUp = true;
                }
            },
            register() {
                let email = document.getElementById("email").value.toLowerCase();
                let userName = document.getElementById("userName").value.toLowerCase();
                let password = document.getElementById("password").value;

                $.post("/api/players", {
                        userName: userName,
                        email: email,
                        password: password
                    })
                    .done(res => {
                        this.login(), console.log(res)
                    })
                    .fail(err => {
                        this.errorMessage = err, console.log(this.errorMessage), this.errorStatus = true
                    })
            },
            createGame() {
                $.post("/api/games")
                    .done(res => {
                        this.res = res, console.log(res), location.replace(`http://localhost:8080/web/game.html?gp=${res.gpid}`)
                    })
                    .fail(err => {
                        this.errorMessage = err, console.log(this.errorMessage), this.errorStatus = true
                    })
            },
            joinGame(i) {
                id = this.gamesData.games[i].gameid;
                $.post(`/api/game/${id}/players`)
                    .done(res => {
                        this.res = res, console.log(res), location.replace(`http://localhost:8080/web/game.html?gp=${res.gpid}`)
                    })
                    .fail(err => {
                        this.errorMessage = err, console.log(this.errorMessage), this.errorStatus = true
                    })
            },
            returnToGame(i, j) {
                gpid = this.gamesData.games[i].players[j].gpid;
                location.replace(`http://localhost:8080/web/game.html?gp=${gpid}`)
            },
            changeTable(table) {
                let change = false;
                if (table == "leaderboard") {
                    this.loginOrBody = true;
                    this.leaderboardOrGames = true;
                    change = true;
                } else if (!change) {
                    this.loginOrBody = true;
                    this.leaderboardOrGames = false;
                }
            },
            getLogin() {
                this.loginOrBody = false;
            },
            getBlueBacground() {
                particlesJS("particles-js", {
                    "particles": {
                        "number": {
                            "value": 160,
                            "density": {
                                "enable": true,
                                "value_area": 800
                            }
                        },
                        "color": {
                            "value": "#ffffff"
                        },
                        "shape": {
                            "type": "circle",
                            "stroke": {
                                "width": 0,
                                "color": "#000000"
                            },
                            "polygon": {
                                "nb_sides": 5
                            },
                            "image": {
                                "src": "img/github.svg",
                                "width": 100,
                                "height": 100
                            }
                        },
                        "opacity": {
                            "value": 1,
                            "random": true,
                            "anim": {
                                "enable": true,
                                "speed": 1,
                                "opacity_min": 0,
                                "sync": false
                            }
                        },
                        "size": {
                            "value": 3,
                            "random": true,
                            "anim": {
                                "enable": false,
                                "speed": 4,
                                "size_min": 0.3,
                                "sync": false
                            }
                        },
                        "line_linked": {
                            "enable": false,
                            "distance": 150,
                            "color": "#ffffff",
                            "opacity": 0.4,
                            "width": 1
                        },
                        "move": {
                            "enable": true,
                            "speed": 1,
                            "direction": "none",
                            "random": true,
                            "straight": false,
                            "out_mode": "out",
                            "bounce": false,
                            "attract": {
                                "enable": false,
                                "rotateX": 600,
                                "rotateY": 600
                            }
                        }
                    },
                    "interactivity": {
                        "detect_on": "canvas",
                        "events": {
                            "onhover": {
                                "enable": true,
                                "mode": "bubble"
                            },
                            "onclick": {
                                "enable": true,
                                "mode": "repulse"
                            },
                            "resize": true
                        },
                        "modes": {
                            "grab": {
                                "distance": 400,
                                "line_linked": {
                                    "opacity": 1
                                }
                            },
                            "bubble": {
                                "distance": 250,
                                "size": 0,
                                "duration": 2,
                                "opacity": 0,
                                "speed": 3
                            },
                            "repulse": {
                                "distance": 400,
                                "duration": 0.4
                            },
                            "push": {
                                "particles_nb": 4
                            },
                            "remove": {
                                "particles_nb": 2
                            }
                        }
                    },
                    "retina_detect": true
                });
                stats = new Stats;
                stats.setMode(0);
                update = function () {
                    stats.begin();
                    stats.end();
                    requestAnimationFrame(update);
                };
                requestAnimationFrame(update);
            }
        }
    })
};

