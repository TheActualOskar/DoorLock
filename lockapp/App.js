import React from 'react';
import { NavigationContainer } from '@react-navigation/native';
import { createNativeStackNavigator } from '@react-navigation/native-stack';
import Login from './Components/Login';
import { UserProvider } from './Components/UserContext';
import HomeScreen from './Components/homeScreen';
import LockDetailsScreen from './Components/LockDetailsScreen';


const Stack = createNativeStackNavigator();

export default function App() {
  return (
    <UserProvider>
      <NavigationContainer>
        <Stack.Navigator>
          <Stack.Screen name="Free-key locks" component={Login}/>
          <Stack.Screen name="homeScreen" component={HomeScreen}/>
          <Stack.Screen name="LockDetails" component={LockDetailsScreen}/>
        </Stack.Navigator>
      </NavigationContainer>
    </UserProvider>
    );
}