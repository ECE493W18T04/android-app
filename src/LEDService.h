/* mbed Microcontroller Library
 * Copyright (c) 2006-2013 ARM Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

#ifndef __BLE_LED_SERVICE_H__
#define __BLE_LED_SERVICE_H__

class WNHService {
public:
    const static uint16_t LED_SERVICE_UUID              = 0xA000;
    const static uint16_t LED_STATE_CHARACTERISTIC_UUID = 0xA001;
    const static uint16_t NAME_STATE_CHARACTERISTIC_UUID = 0xA002;

    WNHService(BLEDevice &_ble, bool initialValueForLEDCharacteristic) :
        ble(_ble), ledState(LED_STATE_CHARACTERISTIC_UUID, &initialValueForLEDCharacteristic), nameState(NAME_STATE_CHARACTERISTIC_UUID, "I AM THE VERY BEST PERSON IN THE WORLD CAUSE I JUST AM AND THAT IS THE WAY IT IS BALHHH. YOU CAN't STOP MEEEE HAHAHA. BEFORE TOT HE HANDS TO THE MOVE IT HADN AND THE TMOVE AGAINS BEFORE. I AM INE IN A CHURCH MUAHAHA.")
    {
        GattCharacteristic *charTable[] = {&ledState, &nameState};
        GattService         ledService(LED_SERVICE_UUID, charTable, sizeof(charTable) / sizeof(GattCharacteristic *));
        ble.gattServer().addService(ledService);
    }

    GattAttribute::Handle_t getValueHandle() const {
        return ledState.getValueHandle();
    }

private:
    BLEDevice                         &ble;
    ReadWriteGattCharacteristic<bool>  ledState;
    ReadWriteArrayGattCharacteristic<char, 300>  nameState;
};

#endif /* #ifndef __BLE_LED_SERVICE_H__ */
