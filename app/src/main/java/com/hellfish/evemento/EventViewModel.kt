package com.hellfish.evemento

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.hellfish.evemento.api.Comment
import com.hellfish.evemento.api.Guest
import com.hellfish.evemento.api.Poll
import com.hellfish.evemento.event.Event
import com.hellfish.evemento.event.transport.TransportItem
import com.hellfish.evemento.event.transport.UserMiniDetail
import com.hellfish.evemento.event.poll.Answer
import com.hellfish.evemento.event.poll.PollObject
import com.hellfish.evemento.event.task.TaskItem


class EventViewModel : ViewModel() {

    var selectedEvent: MutableLiveData<Event> = MutableLiveData()
        private set
    var guests: MutableLiveData<MutableList<Guest>> = MutableLiveData()
        private set
    var rides: MutableLiveData<MutableList<TransportItem>> = MutableLiveData()
        private set
    private var tasks: List<String> = listOf()
    private var polls: MutableLiveData<MutableList<PollObject>> = MutableLiveData()
    var comments: MutableLiveData<MutableList<Comment>> = MutableLiveData()
        private set

    fun getPolls(): MutableLiveData<MutableList<PollObject>> {
        if (polls.value?.isEmpty() ?: true) {
            polls.value = mutableListOf(
                    PollObject.NoVotable("Asdasdesd", listOf(Answer.Closed("Sí", 2), Answer.Closed("No", 1))),
                    PollObject.Votable("Asdasdesdo", listOf(Answer.Open("Sí", 2), Answer.Open("No", 1)))
            )
        }
        return polls
    }

    fun updateView() = select(selectedEvent.value)

    fun selected() = selectedEvent.value

    fun select(event: Event?) {
        selectedEvent.value = event
        comments.value = mutableListOf()
        guests.value = mutableListOf()
        rides.value = mutableListOf()
        polls.value = mutableListOf()
        loadDataFrom(event)     /// TODO: BORRAR AL TERMINAR REFACTOR DE SERVICIOS
    }

    fun loadPolls(callback: (List<Poll>?, Int?) -> (Unit)) {
        selectedEvent.value?.let {
            NetworkManager.getPolls(it) { newPolls, errorMessage ->
                newPolls?.let {
                    //TODO: DESCOMENTAR CUANDO ESTE LISTO EL REFACTOR DE POLLS
//                    polls.value = it.toMutableList()
//                    callback(polls.value, null)
                    return@getPolls
                }
                callback(null, errorMessage)
            }

            return
        }
        callback(null, R.string.api_error_fetching_data)
    }
    fun loadRides(callback: (List<TransportItem>?, Int?) -> (Unit)) {
//TODO: IMPLEMENT
    }

    fun loadComments(onError: (Int?) -> (Unit)) {
        selectedEvent.value?.let {
            NetworkManager.getComments(it) { newComments, errorMessage ->
                newComments?.let { comments.value = it.toMutableList(); return@getComments }
                onError(errorMessage)
            }
            return
        }
        onError(R.string.api_error_fetching_data)
    }

    fun loadGuests(onError: (Int?) -> (Unit)) {
        selectedEvent.value?.let { event ->
            NetworkManager.getAllUsers { users, errorMessage ->
                users?.let {
                    NetworkManager.getGuests(event, users) { newGuests, errorMessage ->
                        newGuests?.let { guests.value = it.toMutableList(); return@getGuests }
                        onError(errorMessage)
                    }
                    return@getAllUsers
                }
                onError(errorMessage)
            }
            return
        }
        onError(R.string.api_error_fetching_data)
    }

    fun loadTasks(callback: (List<TaskItem>?, Int?) -> (Unit)) {
//TODO: IMPLEMENT
    }
    /**
     *The idea is this methos is to called the mocked methods and then replace those methods to pull from server
     */
    /// TODO: BORRAR AL TERMINAR REFACTOR DE SERVICIOS
    private fun loadDataFrom(event: Event?) {
        rides.value = mockedRides()
        tasks = listOf() //TODO load it from Firebase
        polls.value = mutableListOf(
                PollObject.NoVotable("Asdasdesd", listOf(Answer.Closed("Sí", 2), Answer.Closed("No", 1))),
                PollObject.Votable("Asdasdesdo", listOf(Answer.Open("Sí", 2), Answer.Open("No", 1)))
        )
    }

    private fun mockedRides(): MutableList<TransportItem> {
        val transports = ArrayList<TransportItem>()
        val driver1 = UserMiniDetail("Gus", "Sarlanga")
        val driver2 = UserMiniDetail("Gas", "Sarlanga")
        val pass_1_1 = UserMiniDetail("juanR", "Sarlanga")
        val pass_1_2 = UserMiniDetail("juanDs", "Sarlanga")
        val pass_2_1 = UserMiniDetail("NicoB", "Sarlanga")
        val pass_2_2 = UserMiniDetail("NicoS", "Sarlanga")
        val passangers1 = ArrayList<UserMiniDetail>()
        val passangers2 = ArrayList<UserMiniDetail>()
        passangers1.add(pass_1_1)
        passangers1.add(pass_1_2)
        passangers2.add(pass_2_1)
        passangers2.add(pass_2_2)

        transports.add(TransportItem(driver1, passangers1, "casa de gus" ,4))
        transports.add(TransportItem(driver2, passangers2, "casa de gas",3))
        return transports
    }

    fun <T> editList(list: MutableList<T>?, newT: T, comparator: (T, T) -> Boolean) =
            list?.map { oldT -> if (comparator(oldT, newT)) { newT } else { oldT } }?.toMutableList()

    fun add(comment: Comment) {
        comments.value = comments.value?.plus(comment)?.toMutableList()
    }

    fun remove(comment: Comment) {
        comments.value = comments.value?.minus(comment)?.toMutableList()
    }

    fun edit(newComment: Comment) {
        comments.value = editList(comments.value, newComment) { c1, c2 -> c1.commentId == c2.commentId}
    }

    fun add(guest: Guest) {
        guests.value = guests.value?.plus(guest)?.toMutableList()
    }

    fun remove(guest: Guest) {
        guests.value = guests.value?.minus(guest)?.toMutableList()
    }

    fun add(poll: PollObject) {
        polls.value = polls.value?.plus(poll)?.toMutableList()
    }

    fun edit(newPoll: PollObject) {
        polls.value = editList(polls.value, newPoll) { p1, p2 -> p1.question == p2.question }
    }

    fun edit(newTransport: TransportItem) {
        rides.value = editList(rides.value, newTransport) { t1, t2 -> t1.sameTransport(t2) }
    }

    fun add(transportItem: TransportItem) {
        rides.value = rides.value?.plus(transportItem)?.toMutableList()
    }

    fun remove(transportItem: TransportItem) {
        rides.value = rides.value?.minus(transportItem)?.toMutableList()

    }
}

class InvalidEventElementException(override var message: String): Exception(message)
