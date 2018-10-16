getData();

function getData() {
    var data;
    fetch('../api/games')
        .then(response => response.json())
        .then(json => {
            data = json;

            console.log(data);

            getGames(data);
        });
};
function getGames(data) {

}