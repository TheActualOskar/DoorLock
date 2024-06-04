import React, { useState } from 'react'
import { Alert, Pressable, SafeAreaView, StyleSheet, Switch, Text, TextInput, View } from 'react-native'
import axios from 'axios';
import { useUser } from './UserContext'

export default function Login({ navigation }) {

    const [click,setClick] = useState(false);
    const [username,setUsername]=  useState("");
    const [password,setPassword]=  useState("");
    const { setUser } = useUser();

  return (
    <SafeAreaView style={styles.container}>
        <Text style={styles.title}>Login</Text>
        <View style={styles.inputView}>
            <TextInput style={styles.input} placeholder='EMAIL OR USERNAME' value={username} onChangeText={setUsername} autoCorrect={false}
        autoCapitalize='none' />
            <TextInput style={styles.input} placeholder='PASSWORD' secureTextEntry value={password} onChangeText={setPassword} autoCorrect={false}
        autoCapitalize='none'/>
        </View>
        <View style={styles.rememberView}>
            <View style={styles.switch}>
                <Switch  value={click} onValueChange={setClick} trackColor={{true : "green" , false : "gray"}} />
                <Text style={styles.rememberText}>Remember Me</Text>
            </View>
            <View>
                    <Text style={styles.forgetText}>Forgot Password?</Text>
            </View>
        </View>

        <View style={styles.buttonView}>
            <Pressable style={styles.button} onPress={() => login(username, password, navigation, setUser)}>
                <Text style={styles.buttonText}>LOGIN</Text>
            </Pressable>
        </View>

        <Text style={styles.footerText}>Don't Have Account?<Text style={styles.signup}>  Sign Up</Text></Text>

        
    </SafeAreaView>
  )
}

function login(username, password, navigation, setUser) {
  console.log("Login pressed");
  const url = "http://id.joachimbaumann.dk:8080/user/login";
  const data = {
    email: username,
    password: password
  };

  axios.post(url, data)
    .then(response => {
      if (response.data.id && response.status === 200) {
            console.log("login successful")
            setUser(response.data.id)
            navigation.navigate('homeScreen');  //Navigate to HomePage after successfully saving user ID
          }
      else {
        Alert.alert("Login Failed", "Please check your username and password and try again.");
      }
    })
    .catch(error => {
      console.error("Error during login:", error);
      Alert.alert("Login Failed", "Unable to connect to the server. Please try again later.");
    });
}

    

const styles = StyleSheet.create({
  container : {
    alignItems : "center",
    paddingTop: 70,
  },
  image : {
    height : 160,
    width : 170
  },
  title : {
    fontSize : 30,
    fontWeight : "bold",
    textTransform : "uppercase",
    textAlign: "center",
    paddingVertical : 40,
    color : "red"
  },
  inputView : {
    gap : 15,
    width : "100%",
    paddingHorizontal : 40,
    marginBottom  :5
  },
  input : {
    height : 50,
    paddingHorizontal : 20,
    borderColor : "red",
    borderWidth : 1,
    borderRadius: 7
  },
  rememberView : {
    width : "100%",
    paddingHorizontal : 50,
    justifyContent: "space-between",
    alignItems : "center",
    flexDirection : "row",
    marginBottom : 8
  },
  switch :{
    flexDirection : "row",
    gap : 1,
    justifyContent : "center",
    alignItems : "center"
    
  },
  rememberText : {
    fontSize: 13
  },
  forgetText : {
    fontSize : 11,
    color : "red"
  },
  button : {
    backgroundColor : "red",
    height : 45,
    borderColor : "gray",
    borderWidth  : 1,
    borderRadius : 5,
    alignItems : "center",
    justifyContent : "center"
  },
  buttonText : {
    color : "white"  ,
    fontSize: 18,
    fontWeight : "bold"
  }, 
  buttonView :{
    width :"100%",
    paddingHorizontal : 50
  },
  optionsText : {
    textAlign : "center",
    paddingVertical : 10,
    color : "gray",
    fontSize : 13,
    marginBottom : 6
  },
  mediaIcons : {
    flexDirection : "row",
    gap : 15,
    alignItems: "center",
    justifyContent : "center",
    marginBottom : 23
  },
  icons : {
    width : 40,
    height: 40,
  },
  footerText : {
    textAlign: "center",
    color : "gray",
  },
  signup : {
    color : "red",
    fontSize : 13
  }
})