# VeggieVision Machine Learning

![Alt text](4.ImageDocumentation/ML1.png)

Machine Learning Task:
- Search for the dataset
- Pre-processing the dataset (Collect, Explore,  and Cleaning)
- Build the model
- Train the model
- Evaluate the model
- Testing the model
- Deploy/convert the model

## Search for the dataset
There are 2 sources for the dataset that we use. The first dataset, we got it from Kaggle:
```
https://www.kaggle.com/datasets/raghavrpotdar/fresh-and-stale-images-of-fruits-and-vegetables
```
The second dataset we got it from roboflow:
```
https://universe.roboflow.com/lakshantha-dissanayake/apple-detection-5z37o/dataset/1/images
```

## Pre-processing the dataset
We collected and explored the dataset that we got, there were 12 classes for the dataset that we got, we also tried to rotate and dilate the existing dataset so that more or less the dataset we used for each class had 600 datasets.

## Build the model
First, we create the generator. This generator will be used to stream image data from the directory in an efficient manner during deep learning model training.
```py
def train_val_generators(train_path, test_path):
    # Path ke dataset
    train_path = 'VeggieVisionSplit/train'
    test_path = 'VeggieVisionSplit/test'

    # Dimensi gambar
    img_width, img_height = 150, 150

    # Persiapan data generator
    train_datagen = ImageDataGenerator(rescale=1./255)
    test_datagen = ImageDataGenerator(rescale=1./255)

    train_generator = train_datagen.flow_from_directory(
        train_path,
        target_size=(img_width, img_height),
        batch_size=32,
        class_mode='categorical'
    )

    test_generator = test_datagen.flow_from_directory(
        test_path,
        target_size=(img_width, img_height),
        batch_size=32,
        class_mode='categorical'
    )

    return train_generator, test_generator
```
```py
# Test your generators
train_generator, test_generator = train_val_generators(train_path, test_path)
```
We set the image size to be used by the model, namely 150x150 pixels. Then use `ImageDataGenerator` so that the image pixel value which is initially 0-255 will be normalized to the range 0-1 by dividing by 255. Then group the images in batches of size 32 and use `class_mode='categorical` which means the class label will be generated in the form of one -hot encoded for multi-class classification problems.

## Train the model
We use a Convolutional Neural Network (CNN) model using Keras, an API for TensorFlow.
```py
def create_model():
    model = tf.keras.models.Sequential([
        tf.keras.layers.Conv2D(32, (3,3), activation='relu', input_shape=(150, 150, 3)),
        tf.keras.layers.MaxPooling2D(2, 2),

        tf.keras.layers.Conv2D(64, (3,3), activation='relu'),
        tf.keras.layers.MaxPooling2D(2, 2),

        tf.keras.layers.Conv2D(128, (3,3), activation='relu'),
        tf.keras.layers.MaxPooling2D(2, 2),

        tf.keras.layers.Conv2D(128, (3, 3), activation='relu'),
        tf.keras.layers.MaxPooling2D((2, 2)),

        tf.keras.layers.Flatten(),

        tf.keras.layers.Dense(512, activation='relu'),

        tf.keras.layers.Dense(len(train_generator.class_indices), activation='softmax')
    ])


    model.compile(optimizer='adam',
                    loss='categorical_crossentropy',
                    metrics=['accuracy'])

    return model
```
We create a Sequential model, where the model layers are added sequentially.
- Conv2D: Convolution layers using 32, 64, and 128 filters each with a kernel size of 3x3 and a ReLU activation function. The first layer receives input images with a size of 150x150 pixels and 3 color channels (RGB).
- MaxPooling2D: A pooling layer that reduces the dimensions of each map feature by selecting the maximum value from a 2x2 area.

Then we change the output from the 3D convolution layer to 1D so that it can be fed into the Dense layer. Then we added a fully connected layer with 512 neurons and a ReLU activation function. Then we use an output layer with as many neurons as there are classes in the training data and a softmax activation function to generate classification probabilities. We also use `adam` optimizer and `categorical_crossentropy` loss function.
```py
model = create_model()

# Train the model
history = model.fit(train_generator,
                    epochs=50,
                    validation_data=test_generator)

```

## Evaluate the model
```py
#Evaluasi model
test_loss, test_acc = model.evaluate(test_generator)
print('\nTest accuracy:', test_acc)
```

## Testing the model
After training and evaluation, we tried to test the model.
```py
# Dimensi gambar
img_width, img_height = 150, 150

# Fungsi untuk melakukan prediksi pada gambar tunggal
def predict_image(model, img_path, img_width, img_height):
    img = image.load_img(img_path, target_size=(img_width, img_height))
    img_array = image.img_to_array(img)
    img_array = np.expand_dims(img_array, axis=0) / 255.0

    prediction = model.predict(img_array)
    confidence = np.max(prediction) * 100
    predicted_class = np.argmax(prediction, axis=1)
    class_indices = train_generator.class_indices
    class_labels = {v: k for k, v in class_indices.items()}

    predicted_label = class_labels[predicted_class[0]]

    if 'fresh' in predicted_label:
        freshness_percentage = confidence
    else:
        freshness_percentage = 100 - confidence

    return predicted_label, freshness_percentage

# Path ke gambar baru
new_image_path = '/content/VeggieVisionSplit/test/stale_orange/rotated_by_15_Screen Shot 2018-06-12 at 11.30.48 PM.png'

# Lakukan prediksi pada gambar baru
predicted_label, freshness_percentage = predict_image(model, new_image_path, img_width, img_height)
print(f"Prediksi: {predicted_label} dengan persentase kesegaran {freshness_percentage:.2f}%")
```
```py
# Tampilkan gambar dan hasil prediksi dengan kondisi persentase kesegaran
def display_prediction_with_freshness(img_path, predicted_label, freshness_percentage):
    img = image.load_img(img_path, target_size=(img_width, img_height))
    plt.imshow(img)
    plt.title(f'Prediksi: {predicted_label} dengan persentase kesegaran {freshness_percentage:.2f}%')
    plt.axis('off')
    plt.show()

# Tampilkan hasil prediksi
display_prediction_with_freshness(new_image_path, predicted_label, freshness_percentage)
```
You can check the output on the .ipynb file that we uploaded.

## Deploy/convert the model
Last, we convert it to .h5 file and send it to CC path
```py
# Save the model you just trained
model.save("VeggieVision_model.h5")
```