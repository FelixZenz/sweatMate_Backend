//LOGIN
function logout() {
    document.cookie = "jwt=";
    document.cookie = "us=";
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
            console.log("LOGIN FAILED");
            throw Error("HTTP-error: " + res.status);
        }
        else {
            let _jwt = response.headers.get("Authorization");
            document.cookie = "jwt=" + _jwt;
            let result = await response.json();
            //console.log(result);
            alert("You are logged in as " + result.username);
            //await changeSite("account");
            let wert = result.username;
            document.cookie = "us=" + wert;
            // URL für die neue Seite mit dem Parameter
            //var neueSeiteURL = "account.html?us=" + wert;
            window.location.href = "account.html";
        }
    } catch (e){
        console.log(e.toString());
        console.log("LOGIN FAILED");
    }
}

function getURLParameter(name) {
    name = name.replace(/[\[]/, "\\[").replace(/[\]]/, "\\]");
    var regex = new RegExp("[\\?&]" + name + "=([^&#]*)");
    var results = regex.exec(window.location.search);
    return results === null ? "" : decodeURIComponent(results[1].replace(/\+/g, " "));
}

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
    // Abrufen des Werts aus der URL
    let jwt = getCookie("jwt");
    let wert = getCookie("us");
    if(jwt == "" || wert == ""){
        window.location.href = "login.html";
    }
    // Verwendung des Werts
    console.log(wert);
    const url = 'http://localhost:8080/sweatMate_TestProject-1.0-SNAPSHOT/api/plan/details/'+wert;
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
            throw Error("HTTP-error: " + res.status);
            window.location.href = "login.html";
        }
        else {
            document.getElementById("userNameOutputAccountPage").innerHTML = wert;
            //_jwt = response.headers.get("Authorization");
            let result = await response.json();
            //console.log(result);
            let numPlans = Object.keys(result).length;
            document.getElementById("numOfPlans").innerHTML = 'Uploaded Training Plans ('+numPlans+')';
            showAccountDetails(result);
            //alert(document.documentURI);
        }
    } catch (e){
        console.log(e.toString());
        console.log("details failed")
    }

    //console.log(_jsonUser);
    //document.getElementById('userNameOutputAccountPage').innerHTML = userObj.username;
}

function showAccountDetails(obj){
    let html = '';
    obj.forEach(p => html += `
    <tr>
        <!-- Title of training plan 1 -->
        <td>${p.planname}</td>
        <!-- Number of likes for training plan 1 -->
        <td>${p.numLikes}</td>
        <!-- Number of dislikes for training plan 1 -->
        <td>${p.numDislikes}</td>
        <td><img src="../images/icon_delete.svg" alt="Delete"></td>
    </tr>`);
    document.getElementById("accountTable").innerHTML = html;
}

/*
function login(name, pwd) {
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
    fetch(url, init)
        .then(res => {
            if (!res.ok) {
                alert("LOGIN FAILED")
                throw Error("HTTP-error: " + res.status);
            }
            _jwt = res.headers.get("Authorization");
            return res.json();
        })
        .then(json => {
            console.log(json);
            var userObj = json;
            //console.log(userObj);
            alert("You are logged in as " + userObj.username);
            window.location.href = "account.html";
            //getUserDetails();
            alert(document.documentURI);
            getUserDetails(value);
        })
        .catch(err => {
            console.log(err.toString());
            alert("LOGIN FAILED")
        });

}
*/

//create New USER
async function insertNewUser(uname, fname, lname, email, pwd) {
    const url = 'http://localhost:8080/sweatMate_TestProject-1.0-SNAPSHOT/api/login';
    const user = {
        username: uname,
        firstname: fname,
        lastname: lname,
        email: email,
        password: pwd
    };
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
            alert("Register FAILED")
            throw Error("HTTP-error: " + res.status);
        }
        else {
            let result = await response.json();
            console.log(result);
            //alert("You are logged in as " + result.username);
            //await changeSite("account");
            //let wert = result.username;
            //document.cookie = "us=" + wert;
            // URL für die neue Seite mit dem Parameter
            //var neueSeiteURL = "account.html?us=" + wert;
            //window.location.href = "account.html";
            window.location.href = "login.html";
        }
    } catch (e){
        console.log(e.toString());
        alert("Register FAILED");
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

//GET all Plans
var plans = new Map();
function getPlans() {
    const url = 'http://localhost:8080/sweatMate_TestProject-1.0-SNAPSHOT/api/plan/all';
    fetch(url)
        .then(res => {
            if (!res.ok) {
                throw Error("HTTP-error: " + res.status);
            }
            return res.json();
        })
        .then(data => {
            console.log(data);
            //data.forEach(p => beforeAddPlan(p));
           // plaene = data;
            //Object.assign(plaene, )
            displayPlans(data);
        })
        .catch(err => {
            console.log(err);
        });
}
/*
function beforeAddPlan(p){
    addValueToPlans(p.planid, p.planname);
    addValueToPlans(p.planid, p.likes);
    addValueToPlans(p.planid, p.dislikes);
    addValueToPlans(p.planid, p.creator);
}

// Füge Werte zur Map hinzu
function addValueToPlans(key, value) {
    if (plans.has(key)) {
        // Wenn der Schlüssel bereits existiert, füge den Wert dem entsprechenden Array hinzu
        const values = plans.get(key);
        values.push(value);
    } else {
        // Wenn der Schlüssel noch nicht existiert, erstelle ein neues Array mit dem Wert
        plans.set(key, [value]);
    }
}
*/

function displayPlans(data) {
    let html = '';
    data.forEach(p => html += `<tr onclick="handlerPlanExercises(${p.planid});"><td><img src="./../images/img_card.svg" alt="card"></td>
<td style="font-weight: bold;font-size: 20pt">${p.planname}
<p style="font-size: 12pt">by ${p.creator}</p></td>
<td><img src="./../images/icon_like_inactive.svg">${p.numLikes}</td>
<td><img src="./../images/icon_dislike_inactive.svg">${p.numDislikes}</td></tr>`);
    document.getElementById('allPlansTable').innerHTML = html;
}


function handlerPlanExercises(id) {
    let neueSeiteURL = "details.html?id=" + id;
    window.location.href = neueSeiteURL;
}

async function getPlanExercises() {
    let id = getURLParameter("id");
    const url = 'http://localhost:8080/sweatMate_TestProject-1.0-SNAPSHOT/api/plan/' + id;

    try {
        let response = await fetch(url);
        let result = await response.json();
        await displayPlanInfosForExDetails(id);
        displayExercises(result);
        //alert(document.documentURI);
    } catch (e){
        console.log(e.toString());
        alert("GET EXERCISE FAILED")
    }
}

async function displayPlanInfosForExDetails(id){
    const url = 'http://localhost:8080/sweatMate_TestProject-1.0-SNAPSHOT/api/plan/id/' + id;
    let response = await fetch(url);
    let result = await response.json();
    document.getElementById("detailsPlanName").innerHTML = result.planname;
    document.getElementById("detailsUsername").innerHTML = result.creator;
}

async function displayExercises(ex) {
    //console.log(ex);
    let counter = 1;
    for(let elem of ex){
        var url = 'http://localhost:8080/sweatMate_TestProject-1.0-SNAPSHOT/api/exercise/'+ elem.exerciseId;
        let response = await fetch(url)
        let result = await response.json();
        console.log(result);
        document.getElementById("detailsExDes"+counter).innerHTML = elem.details;
        document.getElementById("detialsRepsSets"+counter).innerHTML = elem.num_sets +" Sets with " + elem.num_reps+" Reps";
        //document.getElementById("numSets"+counter).innerHTML = elem.numSets;
        document.getElementById("detailsExName"+counter).innerHTML = result.exerciseName;
        document.getElementById("picture"+counter).innerHTML = `<img src="../images/${result.image}">`;
        counter = counter+1;
    }
    /*
    var url2 = 'http://localhost:8080/sweatMate_TestProject-1.0-SNAPSHOT/api/exercise/'+ result.exerciseId;
    let responseEx = await fetch(url2)


    if(planBefore != plan.planId){
        htmlEx = '<tr><th>Reps</th><th>Sets</th><th>details</th><th>Name</th><th>Image</th></tr>';
    }
    htmlEx += `<tr><td>${plan.num_reps}</td>
    <td>${plan.num_sets}</td><td>${plan.details}</td>
    <td>${ex.exerciseName}</td><td><img src="images/${ex.image}"></td></tr>;`;
    document.getElementById('exercises').innerHTML = htmlEx;
    planBefore = plan.planId;
    */

}

//Single Exercise
function getExercise(exID, plan) {
    const urlEx = './api/exercise/' + `${exID}`;
    fetch(urlEx)
        .then(res => {
            if (!res.ok) {
                throw Error("HTTP-error: " + res.status);
            }
            return res.json();
        })
        .then(json => {
            displayExercises(plan, json)
        })
        .catch(err => console.log(err));
}

let htmlEx = '<tr><th>Reps</th><th>Sets</th><th>details</th><th>Name</th><th>Image</th></tr>';
let planBefore = 0;




var planid = 0;
const createPlan = (planname) => {
    const url = './api/plan/' + `${planname}`;
    const init = {
        method: 'PUT',
        headers: {
            'Authorization': _jwt
        }
    };
    fetch(url, init)
        .then(res => {
            if (!res.ok) {
                document.getElementById("loginMsg").innerHTML = "INSERTING PLAN failed";
                throw Error("HTTP-error: " + res.status);
            }
            return res.json();
        })
        .then(json => {
            document.getElementById("creatorOutput").innerHTML = json.creator;
            document.getElementById("plannameOutput").innerHTML = json.planname;
            planid = json.planid;
            addExercises(json);
        })
        .catch(err => console.log(err));
};

const addExercises = (ex) => {
    document.getElementById("ex").style = "visibility: visible";
}

const fullyPlan = (exerciseName, sets, reps, details) => {
    console.log(exerciseName, sets, reps, details, planid);
    const exerciseId = exercises.get(exerciseName);
    console.log(exerciseId);

}


