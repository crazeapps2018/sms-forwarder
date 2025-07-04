import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.SmsManager
import com.info.smsforwarder.api.ApiClient
import com.info.smsforwarder.model.SmsStatus
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class SmsBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(ctx: Context, intent: Intent) {
        val id = intent.getStringExtra("sms_id") ?: return
        val action = intent.action
        val status = when (action) {
            "SMS_SENT" -> {
                when (resultCode) {
                    Activity.RESULT_OK -> "SENT"
                    SmsManager.RESULT_ERROR_GENERIC_FAILURE -> "FAILED_GENERIC"
                    SmsManager.RESULT_ERROR_NO_SERVICE -> "FAILED_NO_SERVICE"
                    SmsManager.RESULT_ERROR_NULL_PDU -> "FAILED_NULL_PDU"
                    SmsManager.RESULT_ERROR_RADIO_OFF -> "FAILED_RADIO_OFF"
                    else -> "UNKNOWN_ERROR"
                }
            }
            "SMS_DELIVERED" -> {
                when (resultCode) {
                    Activity.RESULT_OK -> "DELIVERED"
                    Activity.RESULT_CANCELED -> "NOT_DELIVERED"
                    else -> "UNKNOWN_DELIVERY_STATUS"
                }
            }
            else -> "UNKNOWN_ACTION"
        }

        // Send to API
        GlobalScope.launch {
            try {
                ApiClient.instance.status(SmsStatus(id, status))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
