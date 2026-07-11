module EEGProcessing



using LinearAlgebra, DSP, Statistics

export compute_features

function bandpower(signal, fs, f_low, f_high)
    psd = abs.(fft(signal)).^2
    freqs = collect(0:fs/length(signal):fs/2)
    band = (freqs .>= f_low) .& (freqs .<= f_high)
    return mean(psd[band])
end

function compute_features(eeg_left::Vector{Float64}, eeg_right::Vector{Float64}, fs::Int)
    bands = Dict("delta" => (0.5, 4), "theta" => (4, 8), "alpha" => (8, 13), "beta" => (13, 30), "gamma" => (30, 45))
    features = Dict{String, Float64}()
    for (b, (f1, f2)) in bands
        pl = bandpower(eeg_left, fs, f1, f2)
        pr = bandpower(eeg_right, fs, f1, f2)
        features[b * "_left"] = pl
        features[b * "_right"] = pr
        features[b * "_asym"] = (pr - pl) / (pr + pl + eps())
    end
    mean_amp = (mean(abs.(eeg_left)) + mean(abs.(eeg_right))) / 2
    low_activity = mean_amp < 0.2
    features["SR"] = low_activity ? 1.0 : 0.0
    return features
end

end # module
