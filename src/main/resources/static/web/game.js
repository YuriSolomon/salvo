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
            playerId: ""
        },
        beforeCreate() {
            let url = new URLSearchParams(window.location.search);
            var id = url.get('gp');
            this.playerId = id;
            fetch(`../api/game_view/${id}`)
                .then(response => response.json())
                .then(json => {
                    this.gameData = json;

                    this.gameData.correntPlayerId = id;
                    console.log(this.gameData);
                    this.buildPlayerTable("ships");
                    this.buildPlayerTable("salvoes");
                    this.getList(this.gameData.ships, this.allShipsLocations);
                    this.getList(this.gameData.salvoes, this.allSalvoesLocations);
                    this.getList(this.gameData.opponentsSalvoes, this.opponentsSalvoes);
                    this.hitTheOpponent = this.gameData.hitTheOpponent;
                    this.getOnTable(this.allShipsLocations, "ships", "blue", this.opponentsSalvoes)
                    this.getOnTable(this.allSalvoesLocations, "salvoes", "red", this.hitTheOpponent)
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
                    tem1 += `<td style="background:yellow">${header[i]}</td>`
                    if (i > 0) {
                        tem2 += `<tr><td style="background:yellow">${body[i]}</td>`
                        for (let j = 1; j < body.length - 1; j++) {
                            tem2 += `<td class="${body[i]+header[j]}"></td>`
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
                                cell.style.background = "green";
                                checked.push(location2);
                                checked.push(location1);
                            } else {
                                cell.style.background = color;
                            }
                        }
                    })
                });
            }
        }
    })
};