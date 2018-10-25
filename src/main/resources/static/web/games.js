getData();

function getData() {
    var app = new Vue({
        el: '#app',
        data: {
            data: [],
            playersScore: []
        },
        beforeCreate() {
            fetch('../api/games')
                .then(response => response.json())
                .then(json => {
                    this.data = json;

                    console.log(this.data);
                    this.getScore(this.data);

                })
        },
        methods: {
            getScore(playersList) {
                playersList.forEach(player => {
                    let playerInfo = {};
                    let totalScore = 0;
                    let wins = 0;
                    let loses = 0;
                    let teids = 0;
                    if (this.playersScore.includes(player.playerId)) {
                        
                    } else {
                        player.player.score.forEach(score => {
                            if(score.gameScore.playersScore == 1) {
                                wins++;
                            } else if (score.gameScore.playersScore == 0.5) {
                                teids++
                            } else {
                                loses++
                            }
                        })
                        totalScore = wins + (teids/2);
                        playerInfo = {"userName": player.player.userName, "totalScore": totalScore, "wins": wins, "loses": loses, "teids": teids};
                        this.playersScore.push(playerInfo);
                    }
                     console.log(this.playersScore);
                })
            }
        }
    })
};