getData();

function getData() {
    var app = new Vue({
        el: '#app',
        data: {
            data: [],
            playersScore: [],
            top25score: []
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
                    player.player.score.forEach(score => {
                        if (score.gameScore.playersScore == 1) {
                            wins++;
                        } else if (score.gameScore.playersScore == 0.5) {
                            teids++
                        } else {
                            loses++
                        }
                    })
                    totalScore = wins + (teids / 2);
                    playerInfo = {
                        "userName": player.player.userName,
                        "totalScore": totalScore,
                        "wins": wins,
                        "loses": loses,
                        "teids": teids
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

                console.log(this.playersScore);
                console.log(this.top25score);
            }
        }
    })
};