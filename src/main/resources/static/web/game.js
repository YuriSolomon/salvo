getData();

function getData() {
    var app = new Vue({
    el: '#app',
    data: {
        gameData: [],
        allLocations: [],
        playerId: ""
    },
    beforeCreate() {
        let url = new URLSearchParams(window.location.search);
        var id = url.get('gp');
        console.log(id);
        this.playerId = id;
        fetch(`../api/game_view/${id}`)
            .then(response => response.json())
            .then(json => {
                this.gameData = json;

                this.gameData.correntPlayerId = id;
                console.log(this.gameData);
                this.buildTable();
                this.getShips(this.gameData);
            })
    },
    methods: {
        buildTable() {
            let table = document.getElementById("grid");
            let tHead = document.createElement("thead");
            let tBody = document.createElement("tbody");
            
            let tem1 = '';
            let tem2 = '';
            let header = [" ",1,2,3,4,5,6,7,8,9,10];
            let body = ["","A","B","C","D","E","F","G","H","I","J"]
            for (let i=0; i<header.length; i++){
                tem1 += `<td>${header[i]}</td>`
               if (i>0) {
                   tem2 += `<tr><td>${body[i]}</td>`
                   for (let j = 1; j < body.length; j++) {
                   tem2 += `<td id="${body[i]+header[j]}"></td>`
                   }
                   tem2 += `</tr>`
               }
            }
            tHead.innerHTML = tem1;
            tBody.innerHTML = tem2;
            
            table.append(tHead, tBody);
        },
        getShips(gameData) {
            gameData.ships.forEach(ship => {
                ship.location.forEach(cell => {
                    this.allLocations.push(cell);
                });
            })
            this.allLocations.forEach(location => {
                    let cell = document.getElementById(location);
                    cell.style.background="blue";
                });
            }
        }
    })
};