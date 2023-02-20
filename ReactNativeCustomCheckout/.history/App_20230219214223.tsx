/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 *
 * @format
 */

import React from 'react';
import {
  SafeAreaView,
  ScrollView,
  StyleSheet,
  Text,
  useColorScheme,
  View,
  NativeModules,
} from 'react-native';

import {Colors, Header} from 'react-native/Libraries/NewAppScreen';

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

function App(): JSX.Element {
  const isDarkMode = useColorScheme() === 'dark';

  const backgroundStyle = {
    backgroundColor: isDarkMode ? Colors.darker : Colors.lighter,
  };

  return (
    <View style={styles.container}>
      <View style={styles.button}>
        <Button onPress={() => this._startCheckout()} title="Start Payment" />
      </View>
      <Text style={styles.response_text}> {this.state.responseText} </Text>
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
