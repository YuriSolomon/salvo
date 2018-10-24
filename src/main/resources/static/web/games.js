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
                    // getScore();
                })
        },
        methods: {
            getScore() {
                this.data.forEach(game => {
                    game.forEach(score => {
                        score.player.score.forEach(playersScore => {
                            


                            this.playersScore.push({"name": score.player.userName,
                                                    "score": (score +=playersScore.playersScore)});
                        })
                    })
                });
                console.log(this.playersScore);
            }
        }
    })
};