getData();

function getData() {
    var app = new Vue({
        el: '#app',
        data: {
            gameData: []
        },
        // beforeCreate() {
        //     fetch("../api/login")
        //         .then(response => response.json())
        //         .then(json => {
        //             this.gameData = json;

        //         })
        // },
        methods: {
            
        }
    })
};