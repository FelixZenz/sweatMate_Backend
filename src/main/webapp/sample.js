//LOGIN
function logout() {
    document.cookie = "jwt=" + "" + "; path=/";
    document.cookie = "us=" + "" + "; path=/";
    window.location.href = "login.html";
}

async function login(name, pwd) {
    const url = 'http://localhost:8080/sweatMate_TestProject-1.0-SNAPSHOT/api/login';
    const user = {
        username: name,
        pwd: pwd
    };
    const init = {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(user)
    };
    try {
        let response = await fetch(url, init);
        if (!response.ok) {
            // alert("Invalid Login Data");
            throw Error("HTTP-error: " + response.status);
            //window.location = "account.html";
        } else {
            let _jwt = response.headers.get("Authorization");
            document.cookie = "jwt=" + _jwt + "; path=/";
            let result = await response.json();
            //console.log(result);
            //alert("You are logged in as " + result.username);
            showSnackbar('green');
            //await changeSite("account");
            let wert = result.username;
            document.cookie = "us=" + wert + "; path=/";
            // URL für die neue Seite mit dem Parameter
            //var neueSeiteURL = "account.html?us=" + wert;
            setTimeout(() => {
                window.location.href = "account.html";
            }, 1000);
        }
    } catch (e) {
        console.log(e.toString());
        //alert("Invalid Login Data");
        showSnackbar('red');
        setTimeout(() => {
            window.location.href = "login.html";
        }, 1000);
    }
}

//everytime when you see a popUp (green or red) this function was called
function showSnackbar(color) {
    var fields = document.querySelectorAll('.snackbar.' + color);
    fields.forEach(function (field) {
        field.className = 'snackbar ' + color + ' show';
        setTimeout(function () {
            field.className = field.className.replace("show", "");
        }, 5000);
    });
}

//to get parameter from URL by name
function getURLParameter(name) {
    name = name.replace(/[\[]/, "\\[").replace(/[\]]/, "\\]");
    var regex = new RegExp("[\\?&]" + name + "=([^&#]*)");
    var results = regex.exec(window.location.search);
    return results === null ? "" : decodeURIComponent(results[1].replace(/\+/g, " "));
}

//to get Cookie value per Name
function getCookie(name) {
    var nameEQ = name + "=";
    var cookies = document.cookie.split(';');
    for (var i = 0; i < cookies.length; i++) {
        var cookie = cookies[i];
        while (cookie.charAt(0) === ' ') {
            cookie = cookie.substring(1, cookie.length);
        }
        if (cookie.indexOf(nameEQ) === 0) {
            return cookie.substring(nameEQ.length, cookie.length);
        }
    }
    return null;
}

//getUserDetails
async function getUserDetails() {
    let jwt = getCookie("jwt");
    let wert = getCookie("us");
    if (jwt == "" || wert == "" || jwt == null || wert == null) {
        window.location.href = "login.html";
    }
    console.log(wert);
    const url = 'http://localhost:8080/sweatMate_TestProject-1.0-SNAPSHOT/api/plan/details/' + wert;
    const init = {
        method: 'GET',
        headers: {
            'Authorization': jwt
        }
    };
    try {
        let response = await fetch(url, init);
        if (!response.ok) {
            console.log("Account details failed");
            throw Error("HTTP-error: " + response.status);
            window.location.href = "login.html";
        } else {
            document.getElementById("userNameOutputAccountPage").innerHTML = wert;
            let result = await response.json();
            console.log(result);
            let numPlans = Object.keys(result).length;
            let likes = 0;
            let dislikes = 0;
            for (element of result) {
                likes = likes + element.numLikes;
                dislikes = dislikes + element.numDislikes;
            }
            document.getElementById("numOfPlans").innerHTML = 'Uploaded Training Plans (' + numPlans + ')';
            document.getElementById("accountEmail").innerHTML = likes + " likes | " + dislikes + " dislikes";
            showAccountDetails(result);
        }
    } catch (e) {
        console.log(e.toString());
        console.log("details failed")
    }
}

//show details for account
function showAccountDetails(obj) {
    let html = '';
    obj.forEach(p => html += `
   <tr>
        <!-- Title of training plan 1 -->
        <td>${p.planname}</td>
        <!-- Number of likes for training plan 1 -->
        <td>${p.numLikes}</td>
        <!-- Number of dislikes for training plan 1 -->
        <td>${p.numDislikes}</td>
        <td><img src="../images/icon_delete.svg" alt="Delete" onclick="deletePlan(${p.planid});"></td>
    </tr>`);
    document.getElementById("accountTable").innerHTML = html;
}

//delete Plan by id
async function deletePlan(id) {
    const url = 'http://localhost:8080/sweatMate_TestProject-1.0-SNAPSHOT/api/plan/' + id;
    const init = {
        method: 'DELETE',
    };
    let response = await fetch(url, init);
    let result = await response.json();
    console.log("Deletet: " + result);
    getUserDetails();
}


//create New USER
async function insertNewUser(uname, fname, lname, email, pwd) {
    if (!uname || !fname || !lname || !email || !pwd || uname == "" ||
        fname == "" || lname == "" || email == "" || pwd == "") {
        showSnackbar('red');
        setTimeout(() => {
            window.location.href = "register.html";
        }, 1000);
    } else {
        const url = 'http://localhost:8080/sweatMate_TestProject-1.0-SNAPSHOT/api/login';
        const user = {
            username: uname,
            firstname: fname,
            lastname: lname,
            email: email,
            password: pwd
        }
        const init = {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(user)
        };
        try {
            let response = await fetch(url, init);
            if (!response.ok) {
                throw Error("HTTP-error: " + response.status);
            }
            let result = await response.json();
            showSnackbar('green');
            setTimeout(() => {
                window.location.href = "login.html";
            }, 1000);
        } catch (e) {
            console.log(e.toString());
            showSnackbar('red');
            setTimeout(() => {
                window.location.href = "register.html";
            }, 1000);
        }
    }

}

var exercises = new Map();
//GET all Exercises
function getExercises() {
    const url = 'http://localhost:8080/sweatMate_TestProject-1.0-SNAPSHOT/api/exercise';
    fetch(url)
        .then(res => {
            if (!res.ok) {
                throw Error("HTTP-error: " + res.status);
            }
            return res.json();
        })
        .then(data => {
            console.log(data);
            data.forEach(e => exercises.set(e.exerciseName, e.exerciseID));
            console.log(exercises);
        })
        .catch(err => {
            console.log(err);
        });
}

//sortieren
function sort(criteria) {
    const url = 'http://localhost:8080/sweatMate_TestProject-1.0-SNAPSHOT/api/plan/all/' + criteria;
    fetch(url)
        .then(res => {
            if (!res.ok) {
                throw Error("HTTP-error: " + res.status);
            }
            return res.json();
        })
        .then(data => {
            console.log(data);
            displayPlans(data);
        })
        .catch(err => {
            console.log(err);
        });
}

//GET all Plans (Standardsortierung = best)
function getPlans() {
    sort('best');
}

//anzeigen der Pläne (discover)
function displayPlans(data) {
    let html = '';
    data.forEach(p => html +=
        `
    <!-- Training Plan Cards -->
    <div class="card">
      <!-- Upper section of the card -->
      <div class="card-upper">
        <img src="./../images/img_card.svg" alt="card">
      </div>

      <!-- Lower section of the card -->
      <div class="card-lower">
        <!-- Card title -->
        <h2>${p.planname}</h2>

        <!-- Card subtitle -->
        <p class="subtitle">by ${p.creator}</p>

        <!-- Card lower part -->
        <div class="card-lower-part">
          <!-- Card likes container -->
       <!-- Card likes container -->
          <div class="card-likes-container">
            <!-- Likes -->
            <p style="font-weight: bold; color: #28B406">${p.numLikes} Likes</p>
            <!--<img src="./../images/icon_like_inactive.svg" alt="icon">-->


            <!-- Dislikes -->
            <p style="font-weight: bold; color: #D20303">${p.numDislikes} Dislikes</p>
            <!--<img src="./../images/icon_dislike_inactive.svg" alt="icon">-->

          </div>

          <!-- Learn more link -->
          <div>
            <a class="card-link" onclick="handlerPlanExercises(${p.planid});">Learn more</a>
          </div>
        </div>
      </div>
    </div>
    `);

    document.getElementById("cardOutput").innerHTML = html;
}

//Überleitefunktion, um die Übungen für einen Plan zu bekommen
function handlerPlanExercises(id) {
    let neueSeiteURL = "details.html?id=" + id;
    window.location.href = neueSeiteURL;
}

//Übungen für einen Plan bekommen
async function getPlanExercises() {
    let id = getURLParameter("id");
    const url = 'http://localhost:8080/sweatMate_TestProject-1.0-SNAPSHOT/api/plan/' + id;
    try {
        let response = await fetch(url);
        let result = await response.json();
        await displayPlanInfosForExDetails(id);
        console.log(result);
        displayExercises(result);
    } catch (e) {
        console.log(e.toString());
        alert("GET EXERCISE FAILED")
    }
    let user = getCookie("us");
    let jwt = getCookie("jwt");
    let pID = getURLParameter("id");
    if (user && jwt && pID) {
        const url2 = 'http://localhost:8080/sweatMate_TestProject-1.0-SNAPSHOT/api/plan/liked/' + user + '/' + pID;
        const init2 = {
            method: 'GET',
            headers: {
                'Authorization': jwt
            }
        }
        try {
            let response2 = await fetch(url2, init2);
            if (!response2.ok) {
                console.log("GET LIKED PLAN FAILED");
                throw Error("HTTP-error: " + response2.status);
            } else {
                let rate = await response2.json();
                console.log(rate);
                if (rate == 1) {
                    document.getElementById("likeBtn").src = "../images/icon_like_active.svg";
                    document.getElementById("dislikeBtn").src = "../images/icon_dislike_inactive.svg";
                } else if (rate == -1) {
                    document.getElementById("likeBtn").src = "../images/icon_like_inactive.svg";
                    document.getElementById("dislikeBtn").src = "../images/icon_dislike_active.svg";
                }
            }
        } catch (e) {
            console.log(e.toString());
            console.log("get liked plans");
        }
    }
}

//Funktion um Informationen über den Plan bei den Details auszugeben
async function displayPlanInfosForExDetails(id) {
    const url = 'http://localhost:8080/sweatMate_TestProject-1.0-SNAPSHOT/api/plan/id/' + id;
    let response = await fetch(url);
    let result = await response.json();
    document.getElementById("detailsPlanName").innerHTML = result.planname;
    document.getElementById("detailsUsername").innerHTML = result.creator;
    var currentUrl = window.location.href;
    // Neuen Parameter hinzufügen (z.B. "?unm=res.username")
    var newUrl = currentUrl + (currentUrl.indexOf('?') === -1 ? '?' : '&') + 'unm=' + result.creator;
    window.history.replaceState({}, '', newUrl);
}

//Funktion um die Übugen bei den Details auzugeben
async function displayExercises(ex) {
    //console.log(ex);
    let html = "";
    for (let elem of ex) {
        var url = 'http://localhost:8080/sweatMate_TestProject-1.0-SNAPSHOT/api/exercise/' + elem.exerciseId;
        let response = await fetch(url);
        let result = await response.json();
        console.log(result);
        html +=
            `
        <div class="exercise-output-top">
        <div class="exercise-output-picture"><img src="../images/${result.image}" style="position: relative;width: 200px;height: auto;"></div>
        <div class="exercise-output">
        <div style="margin-bottom: 50px;">
        <!-- Exercise Name -->
        <h2>${result.exerciseName}</h2>
        <div class="exercise-description">
          <!-- Exercise Description -->
          <p>${elem.details}
          </p>
          <!-- Reps and Sets Information -->
          <h5>${elem.num_sets + " Sets with " + elem.num_reps + " Reps"}</h5>
        </div>
      </div>
    </div>
  </div>
        `;
    }
    document.getElementById("detailsOutput").innerHTML = html;
}

//ist user berechtigt?
function checkUser() {
    let user = getCookie("us");
    let jwt = getCookie("jwt");
    if (!user || !jwt || user == "" || jwt == "") {
        showSnackbar('red');
        setTimeout(() => {
            window.location.href = "discover.html";
        }, 1000);
    }
}

//Plan hinzufügen
async function addPlan() {
    let planname = document.getElementById("name-tp").value;
    let user = getCookie("us");
    let jwt = getCookie("jwt");
    const url = 'http://localhost:8080/sweatMate_TestProject-1.0-SNAPSHOT/api/plan/' + planname + '/' + user;
    const init = {
        method: 'PUT',
        headers: {
            'Authorization': jwt
        }
    }
    try {
        let response = await fetch(url, init);
        if (!response.ok) {
            console.log("CREATE PLAN FAILED");
            throw Error("HTTP-error: " + response.status);
        } else {
            let result = await response.json();
            console.log(result);
            let planID = result.planid;
            console.log(planID);
            var neueSeiteURL = "add.html?pId=" + planID;
            window.location.href = neueSeiteURL;
        }
        console.log(exerciseArr);
    } catch (e) {
        console.log(e.toString());
        console.log("Plan creation failed");
    }
}

//wenn man Plan erstellt, schon den Plannamen sehen kann (bei den Exercises)
async function fillPlanOutput() {
    let id = getURLParameter("pId");
    const url = 'http://localhost:8080/sweatMate_TestProject-1.0-SNAPSHOT/api/plan/id/' + id;
    let response = await fetch(url);
    let result = await response.json();
    document.getElementById("tpNameH2").innerHTML = result.planname;
}

//Eingabe bei Exercises überprüfen
function checkInput() {
    let exElem = document.getElementById("exerciseName1");
    let repElem = document.getElementById("numReps1");
    let setElem = document.getElementById("numSets1");
    if (exElem !== "" && repElem !== "" && setElem !== "") {
        addPlanExercise(exElem, repElem, setElem);
    } else {
        showSnackbar('red');
    }
}

let exerciseArr = [];
//Übung zu Plan Hinzufügen
async function addPlanExercise(exElem, repElem, setElem) {
    let planID = getURLParameter("pId");
    let ex = exElem.value;
    let reps = repElem.value;
    let sets = setElem.value;
    if (ex == "" || reps == "" || sets == "") {
        showSnackbar('red');
    } else {
        let details = document.getElementById("description1").value;
        details = details ? details : "";
        let image;
        let exId;
        const urlEx = 'http://localhost:8080/sweatMate_TestProject-1.0-SNAPSHOT/api/exercise';
        let responseEX = await fetch(urlEx);
        let resultEx = await responseEX.json();
        for (let exerciseObj of resultEx) {
            if (exerciseObj.exerciseName === ex) {
                console.log(exerciseObj);
                image = exerciseObj.image;
                exId = exerciseObj.exerciseID;
                break;
            }
        }
        var exercise1 = new Exercise(ex, reps, sets, details, exId, image);
        exerciseArr.push(exercise1);
        displayAddedExercises(exerciseArr);
        const planEx = {
            planId: planID,
            exerciseId: exId,
            num_sets: sets,
            num_reps: reps,
            details: details
        };
        const initPE = {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(planEx)
        };
        const urlPE = "http://localhost:8080/sweatMate_TestProject-1.0-SNAPSHOT/api/plan/pe";
        try {
            let responsePE = await fetch(urlPE, initPE);
            if (!responsePE.ok) {
                console.log("ADD PE FAILED");
                throw Error("HTTP-error: " + responsePE.status);
            } else {
                let resultPE = await responsePE.json();
                console.log(resultPE);
                showSnackbar('green');
                document.getElementById("exerciseName1").value = "";
                document.getElementById("numSets1").value = "";
                document.getElementById("numReps1").value = "";
                document.getElementById("description1").value = "";
                document.getElementById("addImg1").innerHTML = `<img src="../images/icon_edit.svg" alt="icon">`;
            }
        } catch (e) {
            console.log(e.toString());
            console.log("inlanexercise FAILED");
        }
    }
}

//die hinzugefügte Übung ausgeben
function displayAddedExercises(arr) {
    let html = '';
    for (var elem of arr) {
        html +=
            `
          <div class="exercise-input-top">
    <!-- Exercise input picture -->
    <div class="exercise-input-picture">
      <img src="./../images/${elem.image}" alt="icon">
    </div>
    <!-- Exercise input inputs -->
    <div class="exercise-input-inputs">
      <div style="margin-bottom: 50px;">
        <!-- Name input -->
        <label for="exOut">Name</label>
        <input id="exOut" type="text" value="${elem.exerciseName}" disabled>
      </div>
      <!-- Number of Sets and Number of Reps labels -->
      <label for="numSetsOut">Number of Sets</label>
      <label for="numRepsOut">Number of Reps</label>
      <!-- Number of Sets and Number of Reps inputs -->
      <div class="num-container">
        <input type="number" class="num" id="numSetsOut" value="${elem.sets}" disabled>
        <input type="number" class="num" id="numRepsOut" value="${elem.reps}" disabled>
      </div>
    </div>
    <!-- Exercise input textarea -->
    <div class="exercise-input-textarea">
      <!-- Description label -->
      <label for="descriptionOut">Description</label>
      <!-- Description textarea -->
      <textarea class="description" id="descriptionOut" disabled>${elem.description}</textarea>
    </div>
  </div>  
     `;
    }
    document.getElementById("outputAlreadyAddedExercises").innerHTML = html;
    document.getElementById("finishBtn").style = "visibility: visible;float: right;margin-top: -15px;";
    //document.getElementById("goBackText").style = "visibility: visible;";
}

//Hilfsklasse für das Exercise Array
class Exercise {
    constructor(exerciseName, reps, sets, description, exerciseId, image) {
        this.exerciseName = exerciseName;
        this.reps = reps;
        this.sets = sets;
        this.description = description;
        this.exerciseId = exerciseId;
        this.image = image;
    }
}

//Um die Planerstellung abzuschließen
function finish() {
    window.location.href = "discover.html";
}


//rating
async function ratePlan(type) {
    let planID = getURLParameter("id");
    let user = getCookie("us");
    let jwt = getCookie("jwt");
    if (!user || !jwt || user == "" || jwt == "") {
        showSnackbar('red');
        setTimeout(() => {
            window.location.href = "discover.html";
        }, 1000);
    }
    let rating = (type === 'like') ? 1 : -1;
    const url = 'http://localhost:8080/sweatMate_TestProject-1.0-SNAPSHOT/api/plan/rate';

    const likedPlan = {
        username: user,
        planid: planID,
        rating: rating
    };

    const init = {
        method: 'PUT',
        headers: {
            'Authorization': jwt,
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(likedPlan)
    }
    console.log(init);
    try {
        let response = await fetch(url, init);
        if (!response.ok) {
            console.log("like failed");
            throw Error("HTTP-error: " + response.status);
        } else {
            let result = await response.json();
            console.log(result);
            getPlanExercises();
        }
    } catch (e) {
        console.log(e.toString());
    }
}

//Für alle Passworteingaben
function togglePasswordVisibility(id) {
    const password = document.getElementById(id);
    const type = password.getAttribute(
        'type') === 'password' ? 'text' : 'password';
    password.setAttribute('type', type);
}



