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
            selected: ""
        },
        beforeCreate() {
            let url = new URLSearchParams(window.location.search);
            var id = url.get('gp');
            this.gamePlayerId = id;
            fetch(`../api/game_view/${id}`)
                .then(response => response.json())
                .then(json => {
                    this.gameData = json;
                    if (this.gameData.ships.length == 5) {
                        this.allShips = true;
                        this.buildPlayerTable("salvoes");
                    } else {
                        this.allShips = false;
                    }
                    this.buildPlayerTable("ships");
                    this.getList(this.gameData.ships, this.allShipsLocations);
                    this.getList(this.gameData.salvoes, this.allSalvoesLocations);
                    if (this.gameData.opponentsSalvoes == null) {
                        this.opponentsSalvoes = []
                    } else {
                        this.getList(this.gameData.opponentsSalvoes, this.opponentsSalvoes);
                    }
                    this.hitTheOpponent = this.gameData.hitTheOpponent;
                    this.getOnTable(this.allShipsLocations, "ships", "blue", this.opponentsSalvoes);
                    if (this.allShips) {
                        this.getOnTable(this.allSalvoesLocations, "salvoes", "green", this.hitTheOpponent);
                    }
                    this.getPlacedBackground();
                    let that = this
                    $("#ships").on("click", "td", function () {
                        var theClass = this.className; // "this" is the element clicked
                        that.selected = theClass;
                        that.placeShip(theClass);
                    });

                })
        },
        methods: {
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
                        frame[i].style.background = "yellow"
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
                if (!this.allShips) {
                    this.gameData.ships.forEach(ship => {
                        let placedShip = document.getElementById(ship.type);
                        placedShip.style.background = "green";
                    });
                }
            },
            getBackground(type) {
                if (type == 'horizontal') {
                    let direction = document.getElementById('vertical');
                    direction.style.background = "white";
                } else if (type == 'vertical') {
                    let direction = document.getElementById('horizontal');
                    direction.style.background = "white";
                } else {
                    document.getElementById('carrier').style.background = "white";
                    document.getElementById('battleship').style.background = "white";
                    document.getElementById('destroyer').style.background = "white";
                    document.getElementById('submarine').style.background = "white";
                    document.getElementById('portalBoat').style.background = "white";
                }
                this.getPlacedBackground();
                let ship = document.getElementById(type);
                ship.style.background = "blue";
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
                let newNumber = number
                let newLetter = letter
                let existingShips = [];
                location.push(newLoc);
                this.gameData.ships.forEach(ship => {
                    existingShips.push(ship.type)
                })
                if (!existingShips.includes(type)) {
                    for (let i = 0; i < size - 1; i++) {
                        if (direction == 'horizontal') {
                            if (number + size <= 11) {
                                newNumber++
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
                this.newShip.location = location;
                this.apply();
            },
            createShip() {
                let type = this.newShip.type;
                let location1 = this.newShip.location
                let gpid = this.gameData.gamePlayerId;
                if (type != "" && location1 != []) {
                    console.log(type)
                    console.log(location1)
                    console.log(gpid)
                    $.post({
                        url: `/api/games/players/${gpid}/ships`,
                        data: JSON.stringify({
                            type: type,
                            location: location1
                    }),
                        dataType: "text",
                        contentType: "application/json"
                    })
                    .done(res => {
                        console.log(res),
                        location.reload();
                    })
                    .fail(err => console.log(err))
                }
            },
            apply() {
                if (this.newShip.location.length > 1) {
                    let cell = document.getElementById('ships').getElementsByTagName('td')
                    for (let i = 0; i < cell.length; i++) {
                        cell[i].style.background = "white";
                    }
                    this.getOnTable(this.allShipsLocations, "ships", "blue", this.opponentsSalvoes);
                    this.newShip.location.forEach(cell => {
                        document.getElementById('ships').querySelector(`.${cell}`).style.background = "purple"
                    })
                }
            }
        }
    })
};