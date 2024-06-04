import React, { useEffect, useState } from 'react';
import { StyleSheet, Text, View, Alert, SafeAreaView, TouchableOpacity, ScrollView, Pressable } from 'react-native';
import { StatusBar } from 'expo-status-bar';
import axios from 'axios';
import { useUser } from './UserContext';

function HomeScreen ({navigation}) {
  const { user } = useUser();
  const [locks, setLocks] = useState([]);

  useEffect(() => {
    fetchLocks(user);
  }, [user]);

  const fetchLocks = (user) => {
    const url = `http://id.joachimbaumann.dk:8080/user/doors/${user}`;
    axios.get(url)
      .then(response => {
        console.log('response', response)
        if (response.data && response.data.length > 0) {
          setLocks(response.data);
          console.log("response.data", response.data)
          console.log('locks', locks)
        } else {
          console.log('No door locks found');
        }
      })
      .catch(error => {
        console.error('Error fetching locks:', error);
      });
  };

  return (
    <SafeAreaView style={styles.safeArea}>
      <ScrollView style={styles.scrollView}>
        <View style={styles.userContainer}>
          <Text style={styles.userText}>UserID: {user}</Text>
        </View>
        {locks.map((lock) => (
          <Pressable 
            key={lock.id} 
            style={styles.button} 
            onPress={() => navigateToLockDetails(navigation, lock.id, lock.name)}>
            <Text style={styles.text}>Lock ID: {lock.id} - {lock.name} </Text>
          </Pressable>
        ))}
      </ScrollView>
      <StatusBar style="auto" />
    </SafeAreaView>
  );
};

function navigateToLockDetails(navigation, lockId, lockName){
  navigation.navigate('LockDetails', {lockId: lockId,lockName})

}

const styles = StyleSheet.create({
  safeArea: {
    flex: 1,
    backgroundColor: '#f5f5f5'
  },
  scrollView: {
    marginHorizontal: 20,
  },
  userContainer: {
    alignSelf: 'flex-end',
    marginRight: 20,
    marginTop: 10,
  },
  container: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
  },
  button: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'center',
    paddingVertical: 12,
    paddingHorizontal: 32,
    borderRadius: 4,
    elevation: 3,
    backgroundColor: 'black',
    marginVertical: 10,
  },
  text: {
    fontSize: 16,
    lineHeight: 21,
    fontWeight: 'bold',
    letterSpacing: 0.25,
    color: 'white',
    marginLeft: 8,
  },
  userText: {
    fontSize: 16,
    lineHeight: 21,
    fontWeight: 'bold',
    letterSpacing: 0.25,
    color: 'black',
  }
});

export default HomeScreen;
