```javascript
// ==========================================
// TravelMate - Destination Data
// ==========================================

let destinations = [];


// ==========================================
// Load destinations from Spring Boot
// ==========================================

fetch("http://localhost:8080/api/destinations")

    .then(response => {

        if (!response.ok) {

            throw new Error(
                "Failed to load destinations"
            );

        }

        return response.json();

    })

    .then(data => {

        destinations = data;

        console.log(
            "Destinations loaded:",
            destinations
        );

        // Display destination cards
        displayDestinations();

    })

    .catch(error => {

        console.error(
            "Error loading destinations:",
            error
        );

    });


// ==========================================
// Search Destination
// ==========================================

function searchDestination() {

    let destination =
        document
            .getElementById("destination")
            .value
            .trim();


    let result =
        document.getElementById("result");


    // Empty search
    if (destination === "") {

        result.innerHTML =
            "Please enter a destination.";

        return;

    }


    // Search destination
    let found =
        destinations.find(

            place =>
                place.name.toLowerCase() ===
                destination.toLowerCase()

        );


    // Destination found
    if (found) {

        result.innerHTML = `

    < div class="destination-card" >

                <h2>
                    🎉 ${found.name}
                </h2>

                <p>
                    ${found.description}
                </p>

                <p>
                    <strong>District:</strong>
                    ${found.district}
                </p>

                <p>
                    <strong>State:</strong>
                    ${found.state}
                </p>

                <button
                    onclick="viewDestination(${found.id})">

                    View Destination

                </button>

            </div >

    `;

    }


    // Destination not found
    else {

        result.innerHTML = `

    < p >
    Sorry, ${ destination }
                is not available yet.
            </p >

    `;

    }

}


// ==========================================
// Display All Destinations
// ==========================================

function displayDestinations() {

    let container =
        document.getElementById("destinations");


    // If container doesn't exist
    if (!container) {

        return;

    }


    // Clear old content
    container.innerHTML = "";


    // Create cards
    destinations.forEach(place => {

        container.innerHTML += `

    < div class="destination-card" >

                <h2>
                    ${place.name}
                </h2>

                <p>
                    ${place.description}
                </p>

                <p>
                    <strong>District:</strong>
                    ${place.district}
                </p>

                <p>
                    <strong>State:</strong>
                    ${place.state}
                </p>

                <button
                    onclick="viewDestination(${place.id})">

                    View Destination

                </button>

            </div >

    `;

    });

}


// ==========================================
// Open Destination Details
// ==========================================

function viewDestination(id) {

    let destination =
        destinations.find(
            place => place.id === id
        );


    // Destination not found
    if (!destination) {

        alert(
            "Destination information not found."
        );

        return;

    }


    // Save selected destination
    localStorage.setItem(

        "selectedDestination",

        JSON.stringify(destination)

    );


    // Open destination page
    window.location.href =
        "destination.html";

}
```
