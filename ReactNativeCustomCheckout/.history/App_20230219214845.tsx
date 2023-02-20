/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 *
 * @format
 */

import React from 'react';
import {StyleSheet, Text, Button, View, NativeModules} from 'react-native';

const LINKING_ERROR =
  "The package 'react-native-bridge-payment sdk api' doesn't seem to be linked.";

const PaymentSDKApi = NativeModules.PaymentSDKApi
  ? NativeModules.PaymentSDKApi
  : new Proxy(
      {},
      {
        get() {
          throw new Error(LINKING_ERROR);
        },
      },
    );

function startCheckout() {
  var returnUrl = 'https://merchant.in/pg/process_return';
  var sessionId =
    'session_n5O9jqgE5m-xr-4mHF-54PHvNvz9ClNw43QnmyO7PovpqGBVwfKz64t7M0B8UKnbDhJNO2jQzgxElZ11x9arPCtoIY05m8B78ROa8CE9pYP6';
  PaymentSDKApi.doPayment(sessionId, returnUrl);
}

function App(): JSX.Element {
  return (
    <View style={styles.container}>
      <View style={styles.button}>
        <Button onPress={() => startCheckout()} title="Start Payment" />
      </View>
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
    width: 200,
  },
});

export default App;
