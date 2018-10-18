getData();

function getData() {
    var app = new Vue({
    el: '#app',
    data: {
        data: []
    },
    beforeCreate() {
        let url = new URLSearchParams(window.location.search);
        let id = url.get('gp');
        fetch(`../api/game_view/${id}`)
            .then(response => response.json())
            .then(json => {
                this.data = json;

                console.log(this.data);

            })
    }
  })
};