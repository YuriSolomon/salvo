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

                    // console.log(this.data);
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
                    let ties = 0;
                    player.player.score.forEach(score => {
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
                        "userName": player.player.userName,
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
            sendData() {
                let email = document.getElementById("email").value;
                let password = document.getElementById("password").value;
                console.log(email);
                console.log(password);
            
                $.post( "/api/login", { email: email, password: password })
              .done(function(response) {
                  console.log("logged in")
                  console.log(response)
              });
            }
        }
    })
};