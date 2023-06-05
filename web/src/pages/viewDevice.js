import HTMVaultClient from '../api/htmVaultClient';
import Header from '../components/header';
import BindingClass from "../util/bindingClass";
import DataStore from "../util/DataStore";

/**
 * Logic needed for the view device page of the website.
 */
class ViewDevice extends BindingClass {
    constructor() {
        super();
        this.bindClassMethods(['clientLoaded', 'mount', 'addDeviceToPage', 'addWorkOrdersToPage'], this);
        //this.bindClassMethods(['clientLoaded', 'mount', 'addDeviceToPage', 'addWorkOrdersToPage', 'addSong'], this);
        this.dataStore = new DataStore();
        this.dataStore.addChangeListener(this.addDeviceToPage);
        this.dataStore.addChangeListener(this.addWorkOrdersToPage);
        this.header = new Header(this.dataStore);
        console.log("add device constructor");
    }

    /**
     * Once the client is loaded, get the device metadata and work order list.
     */
    async clientLoaded() {
        const urlParams = new URLSearchParams(window.location.search);
        const deviceId = urlParams.get('controlNumber');
        document.getElementById('control-number').innerText = "Loading Device ...";
        const device = await this.client.getDevice(deviceId);
        this.dataStore.set('device', device);
        document.getElementById('work-orders').innerText = "(loading work orders...)";
        const workOrders = await this.client.getDeviceWorkOrders(deviceId);
        this.dataStore.set('workOrders', workOrders);
    }

    /**
     * Add the header to the page and load the HTMVaultClient.
     */
    mount() {
        document.getElementById('add-new-work-order').addEventListener('click', this.addWorkOrder);

        this.header.addHeaderToPage();

        this.client = new HTMVaultClient();
        this.clientLoaded();
    }

    /**
     * When the device is updated in the datastore, update the device metadata on the page.
     */
    addDeviceToPage() {
        const device = this.dataStore.get('device');
        if (device == null) {
            return;
        }
        document.getElementById('control-number').innerText = device.controlNumber;
        document.getElementById('serial-number').innerText = device.serialNumber;
        document.getElementById('manufacturer').innerText = device.manufacturer;
        document.getElementById('model').innerText = device.model;
        document.getElementById('manufacture-date').innerText = device.manufactureDate;
        document.getElementById('service-status').innerText = device.serviceStatus;
        document.getElementById('facility-name').innerText = device.facilityName;
        document.getElementById('assigned-department').innerText = device.assignedDepartment;
        document.getElementById('compliance-through-date').innerText = device.complianceThroughDate;
        document.getElementById('last-pm-completion-date').innerText = device.lastPmCompletionDate;
        document.getElementById('next-pm-due-date').innerText = device.nextPmDueDate;
        document.getElementById('pm-frequency-months').innerText = device.maintenanceFrequencyInMonths;
        document.getElementById('inventory-add-date').innerText = device.inventoryAddDate;
        document.getElementById('added-by-id').innerText = device.addedById;
        document.getElementById('added-by-name').innerText = device.addedByName;
        document.getElementById('device-notes').innerText = device.notes;
    }

    /**
     * When the work orders are updated in the datastore, update the list of work orders on the page.
     */
    addWorkOrdersToPage() {
        const workOrders = this.dataStore.get('workOrders')

        if (workOrders == null) {
            return;
        }

        let workOrderSummaryHtml = '';
        // table header row
        workOrderSummaryHtml += `<table id="work-orders">
                                   <tr>
                                       <th>Work Order ID</th>
                                       <th>Type</th>
                                       <th>Completion Status</th>
                                       <th>Created</th>
                                       <th>Completed</th>
                                   </tr>`

        let workOrderSummary;
        for (workOrderSummary of workOrders) {
            workOrderSummaryHtml += `
                <tr>
                    <td>${workOrderSummary.workOrderId}</td>
                    <td>${workOrderSummary.workOrderType}</td>
                    <td>${workOrderSummary.workOrderCompletionStatus}</td>
                    <td>${workOrderSummary.creationDateTime}</td>
                    <td>${workOrderSummary.completionDateTime}</td>
                </tr>`
        }
        workOrderSummaryHtml += `</table>`
        document.getElementById('work-orders').innerHTML = workOrderSummaryHtml;
    }

    /**
     * Method to run when the add song playlist submit button is pressed. Call the MusicPlaylistService to add a song to the
     * playlist.
     */
    async addSong() {

        const errorMessageDisplay = document.getElementById('error-message');
        errorMessageDisplay.innerText = ``;
        errorMessageDisplay.classList.add('hidden');

        const playlist = this.dataStore.get('playlist');
        if (playlist == null) {
            return;
        }

        document.getElementById('add-song').innerText = 'Adding...';
        const asin = document.getElementById('album-asin').value;
        const trackNumber = document.getElementById('track-number').value;
        const playlistId = playlist.id;

        const songList = await this.client.addSongToPlaylist(playlistId, asin, trackNumber, (error) => {
            errorMessageDisplay.innerText = `Error: ${error.message}`;
            errorMessageDisplay.classList.remove('hidden');           
        });

        this.dataStore.set('songs', songList);

        document.getElementById('add-song').innerText = 'Add Song';
        document.getElementById("add-song-form").reset();
    }
}

/**
 * Main method to run when the page contents have loaded.
 */
const main = async () => {
    const viewDevice = new ViewDevice();
    viewDevice.mount();
};

window.addEventListener('DOMContentLoaded', main);
