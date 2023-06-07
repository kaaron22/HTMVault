import MusicPlaylistClient from '../api/musicPlaylistClient';
import Header from '../components/header';
import BindingClass from '../util/bindingClass';
import DataStore from '../util/DataStore';

/**
 * Logic needed for the create playlist page of the website.
 */
class CreatePlaylist extends BindingClass {
    constructor() {
        super();
        this.bindClassMethods(['mount', 'submit', 'redirectToViewDevice'], this);
        //this.bindClassMethods(['mount', 'submit', 'redirectToViewPlaylist'], this);
        this.dataStore = new DataStore();
        //this.dataStore.addChangeListener(this.redirectToViewPlaylist);
        this.dataStore.addChangeListener(this.redirectToViewDevice);
        this.header = new Header(this.dataStore);
    }

    /**
     * Add the header to the page and load the MusicPlaylistClient.
     */
    mount() {
        document.getElementById('create').addEventListener('click', this.submit);

        this.header.addHeaderToPage();

        this.client = new MusicPlaylistClient();
    }

    /**
     * Method to run when the create playlist submit button is pressed. Call the MusicPlaylistService to create the
     * playlist.
     */
    async submit(evt) {
        evt.preventDefault();

        const errorMessageDisplay = document.getElementById('error-message');
        errorMessageDisplay.innerText = ``;
        errorMessageDisplay.classList.add('hidden');

        const createButton = document.getElementById('create');
        const origButtonText = createButton.innerText;
        createButton.innerText = 'Loading...';

        const deviceSerialNumber = document.getElementById('serial-number').value;
        const deviceManufacturer = document.getElementById('manufacturer').value;
        const deviceModel = document.getElementById('model').value;
        const deviceFacilityName = document.getElementById('facility-name').value;
        const deviceAssignedDepartment = document.getElementById('assigned-department').value;
        const deviceManufactureDate = document.getElementById('manufacture-date').value;
        const deviceNotes = document.getElementById('notes').value;

        let manufactureDate;
        if (deviceManufactureDate.length < 1) {
            manufactureDate = null;
        } else {
            manufactureDate = deviceManufactureDate;
        }

        let notes;
        if (deviceNotes.length < 1) {
            notes = null;
        } else {
            notes = deviceNotes;
        }

        const device = await this.client.addDevice(deviceSerialNumber, deviceManufacturer,
         deviceModel, deviceFacilityName, deviceAssignedDepartment, manufactureDate, notes, (error) => {
            createButton.innerText = origButtonText;
            errorMessageDisplay.innerText = `Error: ${error.message}`;
            errorMessageDisplay.classList.remove('hidden');
        });
        this.dataStore.set('device', device);
    }

    redirectToViewDevice() {
        const device = this.dataStore.get('device');
        if (device != null) {
            window.location.href = `/device.html?controlNumber=${device.controlNumber}`;
        }
    }

    /**
     * When the playlist is updated in the datastore, redirect to the view playlist page.
     */
    redirectToViewPlaylist() {
        const playlist = this.dataStore.get('playlist');
        if (playlist != null) {
            window.location.href = `/playlist.html?id=${playlist.id}`;
        }
    }
}

/**
 * Main method to run when the page contents have loaded.
 */
const main = async () => {
    const createPlaylist = new CreatePlaylist();
    createPlaylist.mount();
};

window.addEventListener('DOMContentLoaded', main);
