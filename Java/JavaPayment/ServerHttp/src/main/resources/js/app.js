var currentArticle = {
   id: -1,
   name: "",
   price: 0,
   quantity: 0,
   imageUrl:""
};

var listArticle = [];

document.addEventListener("DOMContentLoaded", function () {
   getArticle();

   document.getElementById("updateButton").addEventListener("click", updateDB);
});

function getArticle() {
   var xhr = new XMLHttpRequest();
   var url = "//localhost:8080/getArticle";
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
   resetForm();
   listArticle = [];
   var body = document.getElementById("body");
   currentArticle.id = -1;

   articles.forEach((element) => {
      const newRow = body.insertRow();

      newRow.insertCell(0).textContent = element.id;
      newRow.insertCell(1).textContent = element.name;
      newRow.insertCell(2).textContent = element.price;
      newRow.insertCell(3).textContent = element.quantity;
      listArticle.push(element);
      newRow.onclick = function () {
         if (currentArticle.id != -1) {
            var selectedRows = body.getElementsByClassName("selected");
            selectedRows[0].classList.remove("selected");
         }

         this.classList.add("selected");
         idSelected = parseInt(this.getElementsByTagName("td")[0].innerHTML);
         currentArticle = listArticle[idSelected - 1];

         document.getElementById("idArticle").innerHTML = currentArticle.id;
         document.getElementById("nomArticle").innerHTML = currentArticle.name;
         document.getElementById("prixInput").value = currentArticle.price;
         document.getElementById("stockInput").value = currentArticle.quantity;
         document.getElementById("imageArticle").src = "images/"+currentArticle.imageUrl;
      };
   });
}

function resetTable() {
   var body = document.getElementById("body");

   for (var i = body.rows.length - 1; i >= 0; i--) body.deleteRow(i);
}

function resetForm() {
   document.getElementById("idArticle").innerHTML = "";
   document.getElementById("nomArticle").innerHTML = "";
   document.getElementById("prixInput").value = 0;
   document.getElementById("stockInput").value = 0;
}

function updateCurrentArticleFromForm() {
   currentArticle.price = document.getElementById("prixInput").value;
   currentArticle.quantity = document.getElementById("stockInput").value;
}

function updateDB() {
   var xhr = new XMLHttpRequest();
   var url = "//localhost:8080/update?";
   updateCurrentArticleFromForm();
   var data =
      "id=" +
      currentArticle.id +
      "&price=" +
      currentArticle.price +
      "&quantity=" +
      currentArticle.quantity;

   xhr.onreadystatechange = function () {
      if (xhr.readyState === 4 && xhr.status === 200) {
         getArticle();
      }
   };
   url += data;
   xhr.open("POST", url, true);
   xhr.send();
}
