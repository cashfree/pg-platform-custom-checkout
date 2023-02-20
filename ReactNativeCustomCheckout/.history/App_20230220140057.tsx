/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 *
 * @format
 */

import React, {useState} from 'react';
import {StyleSheet, Button, Text, View} from 'react-native';

import {NativeModules} from 'react-native';
const {PaymentSDKApi} = NativeModules;

function App(): JSX.Element {
  function startCheckout() {
    var returnUrl = 'https://merchant.in/pg/process_return';
    var sessionId =
      'session_n5O9jqgE5m-xr-4mHF-54PHvNvz9ClNw43QnmyO7PovpqGBVwfKz64t7M0B8UKnbDhJNO2jQzgxElZ11x9arPCtoIY05m8B78ROa8CE9pYP6';
    PaymentSDKApi.doPayment(
      sessionId,
      returnUrl,
      (paymentReturnUrl: any, paymentStatus: any) => {
        console.log(paymentReturnUrl);
        setPaymentStatus(paymentStatus);
      },
    );
  }
  const [paymentStatus, setPaymentStatus] = useState('Payment Status here');
  return (
    <View style={styles.container}>
      <Text style={styles.text}> Please click here to start payment. </Text>
      <View style={styles.button}>
        <Button onPress={() => startCheckout()} title="Start Payment" />
      </View>

      <Text style={styles.text}> {paymentStatus} </Text>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    backgroundColor: '#eaeaea',
    alignItems: 'center',
    flexDirection: 'column',
    flex: 1,
  },
  button: {
    color: '#61aafb',
    margin: 8,
    marginTop: 30,
    width: 200,
  },
  text: {
    color: '#212121',
    marginTop: 50,
  },
});

export default App;
