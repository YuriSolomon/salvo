getData();

function getData() {
    var app = new Vue({
    el: '#app',
    data: {
        data: []
    },
    beforeCreate() {
        fetch('../api/games')
            .then(response => response.json())
            .then(json => {
                this.data = json;

                console.log(this.data);

            })
    }
  })
};