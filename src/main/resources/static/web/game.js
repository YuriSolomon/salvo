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
                type: null,
                direction: null,
                location: null,
                numberOfCells: null,
                firstCell: null
            }
        },
        beforeCreate() {
            let url = new URLSearchParams(window.location.search);
            var id = url.get('gp');
            this.gamePlayerId = id;
            fetch(`../api/game_view/${id}`)
                .then(response => response.json())
                .then(json => {
                    this.gameData = json;

                    console.log(this.gameData);

                    if (this.gameData.ships.length == 5) {
                        this.allShips = true;
                        this.buildPlayerTable("salvoes");
                        this.getList(this.gameData.opponentsSalvoes, this.opponentsSalvoes);
                    } else {
                        this.allShips = false;
                    }

                    this.buildPlayerTable("ships");
                    this.getList(this.gameData.ships, this.allShipsLocations);
                    this.getList(this.gameData.salvoes, this.allSalvoesLocations);
                    this.hitTheOpponent = this.gameData.hitTheOpponent;
                    this.getOnTable(this.allShipsLocations, "ships", "blue", this.opponentsSalvoes);
                    this.getOnTable(this.allSalvoesLocations, "salvoes", "green", this.hitTheOpponent);
                    
                })
        },
        mounted() {
                $("#ships").on("click", "td", function() {
                    var theClass = this.className;  // "this" is the element clicked
                    console.log(theClass.length);
                    console.log(theClass);
                });
            
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
                    tem1 += `<td style="background:yellow">${header[i]}</td>`
                    if (i > 0) {
                        tem2 += `<tr><td style="background:yellow">${body[i]}</td>`
                        for (let j = 1; j < body.length - 1; j++) {
                            let cell = `${body[i]+header[j]}`
                            tem2 += `<td class="${cell}"><div class="ds"/></td>`
                        }
                        tem2 += `<td style="background:yellow">${body[i]}</td></tr>`
                    }
                }
                tem2 += `<tr>`;
                for (let k = 0; k < header.length; k++) {
                    tem2 += `<td style="background:yellow">${header[k]}</td>`
                }
                tem2 += `</tr>`
                tem1 += `<td style="background:yellow" />`
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
                list1.forEach(location1 => {
                    let checked = [];
                    let cell = document.getElementById(gridId).querySelector(`.${location1}`);
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
                });
            },
            back() {
                location.replace(`http://localhost:8080/web/games.html`);
            },
            getBackground(type) {
                    let ship1 = document.getElementById('carrier');
                    let ship2 = document.getElementById('battleship');
                    let ship3 = document.getElementById('destroyer');
                    let ship4 = document.getElementById('submarine');
                    let ship5 = document.getElementById('portalBoat');
                    ship1.style.background = "white";
                    ship2.style.background = "white";
                    ship3.style.background = "white";
                    ship4.style.background = "white";
                    ship5.style.background = "white";
                    if (type == 'horizontal') {
                        let direction = document.getElementById('vertical');
                        direction.style.background = "white";
                    } else if (type == 'vertical') {
                        let direction = document.getElementById('horizontal');
                        direction.style.background = "white";
                    }
                

                this.gameData.ships.forEach(ship => {
                    let placedShip = document.getElementById(ship);
                    placedShip.style.background = "green";
                });
                
                let ship = document.getElementById(type);
                ship.style.background = "blue";
            },
            pickShip(type, i) {
                this.newShip.type = type;
                this.newShip.location = null;
                this.newShip.numberOfCells = i;
                
                console.log(type);
                this.getBackground(`${type}`);
            },
            pickShipsDirection(direction) {
                this.newShip.direction = direction

                console.log(direction);
                this.getBackground(`${direction}`);
            },
            placeShip(location1) {
                
            },
            createShip() {
                type = this.newShip.type;
                location = this.newShip.location

                if (type != null && loction != null) {
                    $.post(`/games/players/${gpid}/ships`, { type: type, location: location })
                    .done(res=> {
                        location.reload,
                        console.log(res), 
                        this.newShip.type = null,
                        this.newShip.location = null,
                        this.newShip.numberOfCells = null,
                        this.newShip.firstCell = null
                    })
                    .fail(err=> {this.errorMessage = err, console.log(this.errorMessage), this.errorStatus = true})
                }
            }
        }
    })
};

