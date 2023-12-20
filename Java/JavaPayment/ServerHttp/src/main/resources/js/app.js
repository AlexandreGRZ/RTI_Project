var idSelected = -1;

document.addEventListener("DOMContentLoaded", function () {
   var table = document.getElementById("tabletest");
   var rows = table.getElementsByTagName("tr");

   for (var i = 0; i < rows.length; i++) {
      var currentRow = rows[i];

      currentRow.onclick = function () {
         var selectedRows = table.getElementsByClassName("selected");
         for (var j = 0; j < selectedRows.length; j++) {
            selectedRows[j].classList.remove("selected");
         }

         this.classList.add("selected");
         idSelected = parseInt(this.getElementsByTagName("td")[0].innerHTML);
      };
   }

   document
      .getElementById("updateButton")
      .addEventListener("click", getArticle());
});

function getArticle() {
   var xhr = new XMLHttpRequest();
   var url = "http://localhost:8080/getArticle";
   xhr.onreadystatechange = function () {
      if (xhr.readyState === 4 && xhr.status === 200) {
         var reponseData = JSON.parse(xhr.responseText);
         tableUpdate(reponseData);
      }
   };
   xhr.open("GET", url, true);
   xhr.send();
}

function tableUpdate(articles) {
   resetTable();
   var body = document.getElementById("body");

   articles.forEach((element) => {
      const newRow = body.insertRow();

      newRow.insertCell(0).textContent = element.id;
      newRow.insertCell(1).textContent = element.name;
      newRow.insertCell(2).textContent = element.price;
      newRow.insertCell(3).textContent = element.quantity;
   });
}

function resetTable() {
   var body = document.getElementById("body");

   for (var i = body.rows.length - 1; i >= 0; i--) body.deleteRow(i);
}
