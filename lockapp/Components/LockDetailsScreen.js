import React, { useState, useEffect } from 'react';
import { View, Text, Alert, Pressable, StyleSheet, FlatList, Modal, TouchableOpacity } from 'react-native';
import axios from 'axios';
import { useUser } from './UserContext';

const LockDetailsScreen = ({ route, navigation }) => {
  const { user } = useUser();
  const { lockId, lockName } = route.params;
  const [lockDetails, setLockDetails] = useState(null);
  const [elapsedTime, setElapsedTime] = useState(0);
  const [modalVisible, setModalVisible] = useState(false);

  const fetchLockDetails = () => {
    const url = `http://id.joachimbaumann.dk:8080/user/doors/${user}`;
    axios.get(url)
      .then(response => {
        const lock = response.data;  // Access the first element
        setLockDetails(lock[0]);
        setElapsedTime(0); // Reset elapsed time on new fetch
      })
      .catch(error => {
        console.error('Failed to fetch lock details:', error);
        Alert.alert('Error', 'Failed to fetch lock details. Please try again.');
      });
  };

  useEffect(() => {
    fetchLockDetails(); // Initial fetch

    const fetchInterval = setInterval(() => {
      fetchLockDetails();
    }, 10000); // Fetch every 20 seconds

    const elapsedTimeInterval = setInterval(() => {
      setElapsedTime(prevElapsedTime => prevElapsedTime + 1);
    }, 1000); // Update elapsed time every second

    return () => {
      clearInterval(fetchInterval); // Cleanup fetch interval on component unmount
      clearInterval(elapsedTimeInterval); // Cleanup elapsed time interval on component unmount
    };
  }, [lockId]);

  if (!lockDetails) {
    return <Text>Loading lock details...</Text>;
  }

  //const statusStyle = lockDetails.status.toLowerCase() === 'online' ? styles.onlineStatus : styles.offlineStatus;

  const formatElapsedTime = (lastCheckIn) => {
    const currentTime = new Date();
    const checkInTime = new Date(lastCheckIn);
    const elapsedTimeInSeconds = Math.floor((currentTime - checkInTime) / 1000);

    const mins = Math.floor(elapsedTimeInSeconds / 60);
    const secs = elapsedTimeInSeconds % 60;
    return `${mins}m ${secs}s`;
  };

  const renderLogItem = ({ item }) => (
    <View style={styles.logEntry}>
      <Text style={styles.logTimestamp}>{new Date(item.timestamp).toLocaleString()}</Text>
      <Text style={styles.logMessage}>{item.message}</Text>
    </View>
  );

  return (
    <View style={styles.container}>
      <Text style={styles.title}>{lockName}</Text>
      <View>
        <Text style={styles.status}>
          Status of Lock: <Text >{lockDetails.status}</Text>
        </Text>
        <Text style={styles.updateTime}>
          Last seen online: {formatElapsedTime(lockDetails.lastCheckIn)} ago
        </Text>
      </View>
      <View style={styles.buttonView}>
        <Pressable style={[styles.button, styles.lockButton]} onPress={() => lock(user, lockId)}>
          <Text style={styles.buttonText}>Lock</Text>
        </Pressable>
        <Pressable style={[styles.button, styles.unlockButton]} onPress={() => unlock(user, lockId)}>
          <Text style={styles.buttonText}>Unlock</Text>
        </Pressable>
      </View>
      <Pressable style={styles.logButton} onPress={() => setModalVisible(true)}>
        <Text style={styles.buttonText}>Show Logs</Text>
      </Pressable>

      <Modal
        animationType="slide"
        transparent={true}
        visible={modalVisible}
        onRequestClose={() => setModalVisible(false)}
      >
        <View style={styles.modalContainer}>
          <View style={styles.modalView}>
            <Text style={styles.modalTitle}>Log Entries</Text>
            <FlatList
              data={lockDetails.logEntries}
              renderItem={renderLogItem}
              keyExtractor={item => item.id.toString()}
              contentContainerStyle={styles.logList}
            />
            <Pressable style={styles.closeButton} onPress={() => setModalVisible(false)}>
              <Text style={styles.buttonText}>Close</Text>
            </Pressable>
          </View>
        </View>
      </Modal>
    </View>
  );
};

function lock(user, lockId) {
  const url = `http://id.joachimbaumann.dk:8080/user/lock`;
  const data = {
    doorID: lockId,
    userID: user 
  };
  axios.post(url, data)
    .then(response => {
      Alert.alert('Door locked', 'Confirmation: The lock command has successfully been received.');
      console.log("lock successful", response)
    })
    .catch(error => {
      console.error('Error:', error);
      console.log("Door lock failed', 'An error occurred and the door could not be locked")
      Alert.alert('Door lock failed', 'An error occurred and the door could not be locked');
    });
}

function unlock(user, lockId) {
  const url = `http://id.joachimbaumann.dk:8080/user/unlock`;
  const data = {
    doorID: lockId,
    userID: user 
  };
  axios.post(url, data)
    .then(response => {
      Alert.alert('Door Unlocked', 'Confirmation: The unlock command has successfully been received.');
      console.log("unlock successful", response)
    })
    .catch(error => {
      console.error('Error:', error);
      Alert.alert('Error', 'Failed to unlock the door. Please try again.');
      console.log("data", data)
    });
}

const styles = StyleSheet.create({
  container: {
    alignItems: "center",
    paddingTop: 70,
  },
  title: {
    fontSize: 30,
    fontWeight: "bold",
    textTransform: "uppercase",
    textAlign: "center",
    paddingVertical: 40,
    color: "black"
  },
  buttonView: {
    flexDirection: "row",
    justifyContent: "space-evenly",
    width: "100%",
    paddingHorizontal: 50
  },
  button: {
    backgroundColor: "red",
    borderColor: "gray",
    borderWidth: 1,
    borderRadius: 5,
    alignItems: "center",
    justifyContent: "center",
    marginHorizontal: 10,
  },
  lockButton: {
    width: 80,  
    height: 45, 
  },
  unlockButton: {
    width: 120, 
    height: 45,  
  },
  buttonText: {
    color: "white",
    fontSize: 18,
    fontWeight: "bold"
  },
  status: {
    fontSize: 18,
    marginBottom: 16,
    color: 'black', 
  },
  offlineStatus: {
    color: 'red',
    fontWeight: 'bold',
  },
  onlineStatus: {
    color: 'green',
    fontWeight: 'bold',
  },
  updateTime: {
    fontSize: 16,
    marginTop: 10,
    color: 'black',
  },
  logButton: {
    marginTop: 20,
    backgroundColor: 'blue',
    padding: 10,
    borderRadius: 5,
  },
  logList: {
    paddingHorizontal: 0,
    width: '100%'
  },
  logEntry: {
    marginBottom: 10,
    padding: 10,
    backgroundColor: '#f9f9f9',
    borderBottomWidth: 1,
    borderBottomColor: '#ddd',
  },
  logTimestamp: {
    fontSize: 14,
    color: '#555',
  },
  logMessage: {
    fontSize: 16,
    color: '#333',
  },
  modalContainer: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: 'rgba(0, 0, 0, 0.5)',
  },
  modalView: {
    width: '80%',  // Make modal take most of the screen width
    height: '70%',
    backgroundColor: 'white',
    borderRadius: 10,
    padding: 20,
    alignItems: 'center',
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.25,
    shadowRadius: 4,
    elevation: 5,
  },
  modalTitle: {
    fontSize: 20,
    fontWeight: 'bold',
    marginBottom: 20,
  },
  closeButton: {
    backgroundColor: 'red',
    padding: 10,
    borderRadius: 5,
    marginTop: 20,
  },
});

export default LockDetailsScreen;
