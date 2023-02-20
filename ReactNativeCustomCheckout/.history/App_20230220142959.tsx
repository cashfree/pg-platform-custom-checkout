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
  var merchantReturnUrl = 'https://merchant.in/pg/process_return';
  function startCheckout() {
    var sessionId =
      'session_DdtRhswlvEnRNSz2zvnNivGF9Asexkmqxit8t3e1YXsuINZIzi2cpIXGqtrRrGFAWrHeBDB8Ec28ukOV7B6lcGCw8mAAjtS72YwFvqthgGEu';
    PaymentSDKApi.doPayment(
      sessionId,
      merchantReturnUrl,
      (paymentReturnUrl: any, paymentStatus: any) => {
        setPaymentStatus(paymentStatus);
        setpaymentReturnUrl(paymentReturnUrl);
      },
    );
  }
  const [paymentStatus, setPaymentStatus] = useState('Payment Status here');
  const [paymentReturnUrl, setpaymentReturnUrl] = useState(merchantReturnUrl);
  return (
    <View style={styles.container}>
      <Text style={styles.text}> Please click here to start payment. </Text>
      <View style={styles.button}>
        <Button onPress={() => startCheckout()} title="Start Payment" />
      </View>

      <Text style={styles.text}> Payment : {paymentStatus} </Text>
      <Text style={styles.text}>Merchant Return URL : {paymentReturnUrl}</Text>
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
