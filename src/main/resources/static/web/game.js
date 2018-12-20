getData();

function getData() {
    var app = new Vue({
        el: '#app',
        data: {
            gameData: [],
            allShipsLocations: [],
            allSalvoesLocations: [],
            opponentsSalvoes: [],
            hitTheOpponent: [],
            gamePlayerId: "",
            allShips: false,
            newShip: {
                type: "",
                direction: "",
                location: [],
                numberOfCells: 0,
                firstCell: ""
            },
            selected: "",
            pickSalvoes: [],
            historyData: [],
            turnsData: [],
            state: "",
            errorStatus: false,
            shot: false,
            shipsList: [{
                    type: "carrier",
                    location: []
                },
                {
                    type: "battleship",
                    location: []
                },
                {
                    type: "destroyer",
                    location: []
                },
                {
                    type: "submarine",
                    location: []
                },
                {
                    type: "portalBoat",
                    location: []
                }
            ]
        },
        beforeCreate() {
            let url = new URLSearchParams(window.location.search);
            var id = url.get('gp');
            this.gamePlayerId = id;
            setInterval(() => {
                fetch(`../api/game_view/${id}`)
                    .then(response => response.json())
                    .then(json => {
                        this.updateData(json)
                    })
            }, 5000)
            fetch(`../api/game_view/${id}`)
                .then(response => response.json())
                .then(json => {
                    this.dispatch(json)
                })
        },
        methods: {
            updateData(json) {
                this.gameData = json;
                        this.turnsData = json.turnsHistory;
                        this.state = json.gameState.gamesState;
                        this.getList(this.gameData.ships, this.allShipsLocations);
                        this.getList(this.gameData.salvoes, this.allSalvoesLocations);
                        this.hitTheOpponent = this.gameData.hitTheOpponent;
                        this.getOnTable(this.allShipsLocations, "ships", "dodgerblue", this.gameData.opponentsHits);
                        if (this.allShips) {
                            this.getOnTable(this.allSalvoesLocations, "salvoes", "deepskyblue", this.hitTheOpponent);
                        }
                        this.getPlacedBackground();
                        if (this.gameData.lastSalvo != null) {
                            if (this.gameData.lastSalvo.length > 0) {
                                this.getLastSalvo();
                            }
                        } else {
                            this.shot = false;
                        }
                        if (this.gameData.gameState.gamesState != "waiting for opponent to shoot a salvo"){
                            this.shot = false;
                        }
            },
            dispatch(json) {
                this.gameData = json;
                    this.turnsData = json.turnsHistory;
                    this.state = json.gameState.gamesState;
                    if (this.gameData.ships.length == 5) {
                        this.allShips = true;
                        this.buildPlayerTable("salvoes");
                    } else {
                        this.allShips = false;
                    }
                    this.buildPlayerTable("ships");
                    this.getList(this.gameData.ships, this.allShipsLocations);
                    this.getList(this.gameData.salvoes, this.allSalvoesLocations);
                    this.hitTheOpponent = this.gameData.hitTheOpponent;
                    this.getOnTable(this.allShipsLocations, "ships", "dodgerblue", this.gameData.opponentsHits);
                    if (this.allShips) {
                        this.getOnTable(this.allSalvoesLocations, "salvoes", "deepskyblue", this.hitTheOpponent);
                    }
                    this.getPlacedBackground();
                    if (this.gameData.lastSalvo != null) {
                        if (this.gameData.lastSalvo.length > 0) {
                            this.getLastSalvo();
                        }
                    }
                    let that = this
                    $("#ships").on("click", "td", function () {
                        var theClass = this.className;
                        that.selected = theClass;
                        that.placeShip(theClass);
                    });
                    $("#salvoes").on("click", "td", function () {
                        var theClass = this.className;
                        that.selected = theClass;
                        that.placeSalvoes(theClass);
                    });
                    this.getBlueBacground();
            },
            buildPlayerTable(tableId) {
                let table = document.getElementById(tableId);
                let tHead = document.createElement("thead");
                let tBody = document.createElement("tbody");
                let tem1 = '';
                let tem2 = '';
                let header = ["", 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, ""];
                let body = ["", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", ""]
                for (let i = 0; i < header.length - 1; i++) {
                    tem1 += `<td>${header[i]}</td>`
                    if (i > 0) {
                        tem2 += `<tr><td>${body[i]}</td>`
                        for (let j = 1; j < body.length - 1; j++) {
                            let cell = `${body[i]+header[j]}`
                            tem2 += `<td class="${cell}"><div class="ds"/></td>`
                        }
                        tem2 += `<td>${body[i]}</td></tr>`
                    }
                }
                tem2 += `<tr>`;
                for (let k = 0; k < header.length; k++) {
                    tem2 += `<td>${header[k]}</td>`
                }
                tem2 += `</tr>`
                tem1 += `<td/>`
                tHead.innerHTML = tem1;
                tBody.innerHTML = tem2;
                table.append(tHead, tBody);
            },
            getList(gameData, list) {
                gameData.forEach(element => {
                    element.location.forEach(cell => {
                        list.push(cell);
                    });
                })
            },
            getOnTable(list1, gridId, color, list2) {
                let frame = document.getElementById(gridId).getElementsByTagName('td')
                for (let i = 0; i < frame.length; i++) {
                    if (frame[i].className.length < 2) {
                        frame[i].style.background = "rgba(6, 63, 104, 0.3)"
                    }
                }
                list1.forEach(location1 => {
                    let checked = [];
                    let cell = document.getElementById(gridId).querySelector(`.${location1}`);
                    if (list2.length == 0) {
                        cell.style.background = color;
                    } else {
                        list2.forEach(location2 => {
                            if (!checked.includes(location2) && !checked.includes(location1)) {
                                if (location1 == location2) {
                                    cell.style.background = "red";
                                    checked.push(location2);
                                    checked.push(location1);
                                } else {
                                    cell.style.background = color;
                                }
                            }
                        })
                    }
                });
            },
            back() {
                location.replace(`http://localhost:8080/web/games.html`);
            },
            getPlacedBackground() {
                this.shipsList.forEach(ship => {
                    if (ship.location.length > 1) {
                        let placedShip = document.getElementById(ship.type);
                        placedShip.style.background = "green";
                    }
                });
            },
            getBackground(type) {
                if (type == 'horizontal') {
                    let direction = document.getElementById('vertical');
                    direction.style.background = "#232741";
                } else if (type == 'vertical') {
                    let direction = document.getElementById('horizontal');
                    direction.style.background = "#232741";
                } else {
                    document.getElementById('carrier').style.background = "#232741";
                    document.getElementById('battleship').style.background = "#232741";
                    document.getElementById('destroyer').style.background = "#232741";
                    document.getElementById('submarine').style.background = "#232741";
                    document.getElementById('portalBoat').style.background = "#232741";
                }
                this.getPlacedBackground();
                let ship = document.getElementById(type);
                ship.style.background = "dodgerblue";
            },
            pickShip(type, i) {
                this.newShip.type = type;
                this.newShip.location = [];
                this.newShip.numberOfCells = i;
                this.getBackground(`${type}`);
            },
            pickShipsDirection(direction) {
                this.newShip.direction = direction
                this.getBackground(`${direction}`);
            },
            placeShip(location1) {
                let direction = this.newShip.direction;
                let firstCell = location1;
                let size = this.newShip.numberOfCells;
                let split = firstCell.split('')
                let letter = split[0];
                let number = split[1];
                let location = []
                let type = this.newShip.type;
                number = firstCell.match(/\d+/g).map(Number)[0]
                let letterValue = letter.charCodeAt(0) - 64;
                let newLoc = letter + number;
                let newNumber = number;
                let newLetter = letter;
                let existingShips = [];
                if (!this.allShipsLocations.includes(newLoc)) location.push(newLoc);
                this.gameData.ships.forEach(ship => {
                    existingShips.push(ship.type);
                })
                if (!existingShips.includes(type)) {
                    for (let i = 0; i < size - 1; i++) {
                        if (direction == 'horizontal') {
                            if (number + size <= 11) {
                                newNumber++;
                                newLoc = letter + newNumber;
                                if (!this.allShipsLocations.includes(newLoc)) {
                                    location.push(newLoc);
                                }
                            }
                        } else if (direction == 'vertical') {
                            if (letterValue + size <= 11) {
                                newLetter = newLetter.substring(0, newLetter.length - 1) + String.fromCharCode(newLetter.charCodeAt(newLetter.length - 1) + 1)
                                newLoc = newLetter + number;
                                if (!this.allShipsLocations.includes(newLoc)) {
                                    location.push(newLoc);
                                }
                            }
                        }
                    }
                }
                if (location.length != size) {
                    location = [];
                }
                let sizes = [5, 4, 3, 3, 2]
                this.newShip.location = location;
                this.shipsList.forEach((ship, i) => {
                    if (this.newShip.type == ship.type) {
                        if (this.newShip.location.length == sizes[i]) {
                            ship.location = this.newShip.location;
                        }
                    }
                })
                this.apply();
            },
            createShip() {
                let gpid = this.gameData.gamePlayerId;
                $.post({
                        url: `/api/games/players/${gpid}/ships`,
                        data: JSON.stringify(this.shipsList),
                        dataType: "text",
                        contentType: "application/json"
                    })
                    .done(res => {
                        console.log(res),
                            location.reload();
                    })
                    .fail(err => console.log(err))

            },
            apply() {
                if (this.newShip.location.length > 1) {
                    let cell = document.getElementById('ships').getElementsByTagName('td')
                    for (let i = 0; i < cell.length; i++) {
                        cell[i].style.background = "";
                    }
                    this.getOnTable(this.allShipsLocations, "ships", "dodgerblue", this.gameData.opponentsHits);
                    this.shipsList.forEach(ship => {
                        ship.location.forEach(cell => {
                            document.getElementById('ships').querySelector(`.${cell}`).style.background = "purple"
                        })
                    })

                }
            },
            placeSalvoes(theClass) {
                if (!this.shot) {
                    let location1 = theClass;
                    let number = location1.match(/\d+/g).map(Number)[0]
                    if (!this.pickSalvoes.includes(location1)) {
                        if (location1.length == 2 || number == 10) {
                            if (!this.allSalvoesLocations.includes(location1)) {
                                this.pickSalvoes.push(location1);
                            }
                        }
                    }
                    if (this.pickSalvoes.length == 4) {
                        this.pickSalvoes.splice(0, 1);
                    }
                    let cell = document.getElementById('salvoes').getElementsByTagName('td')
                    for (let i = 0; i < cell.length; i++) {
                        cell[i].style.background = "";
                    }
                    this.getOnTable(this.allSalvoesLocations, "salvoes", "deepskyblue", this.hitTheOpponent);
                    this.getPlacedBackground();
                    this.pickSalvoes.forEach(loc => {
                        let el = document.getElementById('salvoes').querySelector(`.${loc}`);
                        el.style.background = "purple";
                    })
                }
            },
            createSalvo() {
                let turn = this.gameData.salvoes.length + 1;
                let location1 = this.pickSalvoes
                let gpid = this.gameData.gamePlayerId;
                if (location1.length == 3) {
                    $.post({
                            url: `/api/games/players/${gpid}/salvoes`,
                            data: JSON.stringify({
                                turn: turn,
                                location: location1
                            }),
                            dataType: "text",
                            contentType: "application/json"
                        })
                        .done(res => {
                            console.log(res),
                                location.reload();
                        })
                        .fail(err => {
                            console.log(err),
                                this.errorStatus = true
                        })
                }
            },
            getLastSalvo() {
                locations = this.gameData.lastSalvo
                locations.forEach(location => {
                    let el = document.getElementById('salvoes').querySelector(`.${location}`);
                    el.style.background = "purple";
                    this.shot = true;
                })
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